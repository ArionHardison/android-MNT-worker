package com.dietmanager.chef.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.text.method.PasswordTransformationMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dietmanager.chef.utils.Utils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.dietmanager.chef.Application;
import com.dietmanager.chef.CountryPicker.Country;
import com.dietmanager.chef.CountryPicker.CountryPicker;
import com.dietmanager.chef.CountryPicker.CountryPickerListener;
import com.dietmanager.chef.R;
import com.dietmanager.chef.api.APIError;
import com.dietmanager.chef.api.ApiClient;
import com.dietmanager.chef.api.ApiInterface;
import com.dietmanager.chef.api.ErrorUtils;
import com.dietmanager.chef.helper.ConnectionHelper;
import com.dietmanager.chef.helper.CustomDialog;
import com.dietmanager.chef.helper.GlobalData;
import com.dietmanager.chef.helper.SharedHelper;
import com.dietmanager.chef.model.Otp;
import com.dietmanager.chef.model.Profile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dietmanager.chef.utils.TextUtils.isValidEmail;

public class Login extends AppCompatActivity {

    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 0;
    CustomDialog customDialog;
    @BindView(R.id.countryImage)
    ImageView countryImage;
    @BindView(R.id.countryNumber)
    TextView countryNumber;
    /*@BindView(R.id.tv_terms_policy)
    TextView tvTermsAndPolicy;*/

    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_mobile_number)
    EditText mobileNo;
    @BindView(R.id.next_btn)
    Button submit;
    String country_code;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    ConnectionHelper connectionHelper;
    private CountryPicker mCountryPicker;
    String GRANT_TYPE = "password";

    String TAG = "Login";
    String device_token, device_UDID;
    @BindView(R.id.et_current_password)
    EditText etCurrentPassword;
    @BindView(R.id.et_current_password_eye_img)
    ImageView etCurrentPasswordEyeImg;
    private void addLink(TextView textView, String patternToMatch,
                         final String link) {
        Linkify.TransformFilter filter = (match, url) -> link;
        Linkify.addLinks(textView, Pattern.compile(patternToMatch), null, null,
                filter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //tvTermsAndPolicy.setText(getString(R.string.login_terms_privacy_policy));
        //addLink(tvTermsAndPolicy, getString(R.string.login_terms_and_conditions_label), getString(R.string.login_terms_and_conditions_url));
        //addLink(tvTermsAndPolicy, getString(R.string.login_privacy_policy_label), getString(R.string.login_privacy_policy_url));

        Application.getInstance().fetchDeviceToken();
        getDeviceToken();
        customDialog = new CustomDialog(Login.this);
        connectionHelper = new ConnectionHelper(this);
        if (SharedHelper.getKey(Login.this, "logged_in").equalsIgnoreCase("1")) {
            startActivity(new Intent(Login.this, Home.class));
            finish();
        }
        mCountryPicker =
                CountryPicker.newInstance(getResources().getString(R.string.select_country));

        // You can limit the displayed countries
        List<Country> countryList = Country.getAllCountries();
        Collections.sort(countryList, new Comparator<Country>() {
            @Override
            public int compare(Country s1, Country s2) {
                return s1.getName().compareToIgnoreCase(s2.getName());
            }
        });

        mCountryPicker.setCountriesList(countryList);
        setListener();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS,
                            Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        }
        etCurrentPasswordEyeImg.setTag(1);
        getDeviceToken();
    }


    public void getOtpVerification(String mobile) {
        if (!connectionHelper.isConnectingToInternet()) {
            Toast.makeText(this, getResources().getString(R.string.check_internet_connection),
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (customDialog != null)
            customDialog.show();

        Call<Otp> call = apiInterface.getOtp(mobile);
        call.enqueue(new Callback<Otp>() {
            @Override
            public void onResponse(@NonNull Call<Otp> call, @NonNull Response<Otp> response) {
                customDialog.cancel();
                if (response.isSuccessful()) {
                    Toast.makeText(Login.this, response.body().getMessage(),
                            Toast.LENGTH_SHORT).show();
                    launchActivity(response.body().getOtp());
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        if (jObjError.has("message")) {
                            Toast.makeText(Login.this, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                        } else if (jObjError.has("phone")) {
                            Toast.makeText(Login.this, jObjError.optString("phone"), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Login.this, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
//                    APIError error = ErrorUtils.parseError(response);
//                    Toast.makeText(Login.this, error.getError(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Otp> call, @NonNull Throwable t) {
                customDialog.dismiss();
                Toast.makeText(Login.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void launchActivity(int otp) {
        Intent intent = new Intent(getApplicationContext(), OTP.class);
        intent.putExtra("otp", otp);
        startActivity(intent);
    }

    private void setListener() {
        mCountryPicker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(Country country) {
//                mCountryNameTextView.setText(name);
//                mCountryIsoCodeTextView.setText(code);
                countryNumber.setText(country.getDialCode());
                countryImage.setImageResource(country.getFlag());
                mCountryPicker.dismiss();
            }
        });
        countryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });
        countryNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });
        getUserCountryInfo();
    }

    private void getUserCountryInfo() {
        Country country = Country.getCountryFromSIM(this);
        if (country != null) {
            countryImage.setImageResource(country.getFlag());
            countryNumber.setText(country.getDialCode());
            country_code = country.getDialCode();
        } else {
            countryImage.setImageResource(R.drawable.flag_au);
            countryNumber.setText("+61");
            country_code = "+61";
        }
    }

    private boolean isValidMobile(String phone) {
        return !(phone == null || phone.length() < 6 || phone.length() > 13) && android.util.Patterns.PHONE.matcher(phone).matches();
    }
    @OnClick({R.id.next_btn,R.id.et_current_password_eye_img,R.id.donnot_have_account})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.next_btn:
                String mobileNumber = country_code + mobileNo.getText().toString();
                String email = etEmail.getText().toString();

                /*if (!isValidMobile(mobileNumber)) {
                    Toast.makeText(this, getResources().getString(R.string.please_enter_valid_number), Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(this,OtpActivity.class));
                }*/
                if (email.isEmpty())
                    Utils.displayMessage(Login.this, getResources().getString(R.string.please_enter_mail_id));
                else if (!isValidEmail(email))
                    Utils.displayMessage(Login.this, getResources().getString(R.string.please_enter_valid_mail_id));
                else if (etCurrentPassword.getText().toString().isEmpty()) {
                    Toast.makeText( this, getResources().getString(R.string.please_enter_password),Toast.LENGTH_LONG).show();
                } else if (etCurrentPassword.getText().toString().length() < 6) {
                    Toast.makeText( this, getResources().getString(R.string.please_enter_minimum_length_password),Toast.LENGTH_LONG).show();
                } else {
                    HashMap<String, String> map = new HashMap<>();
                    /*map.put("mobile", mobileNo.getText().toString());
                    map.put("dial_code", country_code);*/
                    map.put("username",email);
                    map.put("password", etCurrentPassword.getText().toString());
                    //map.put("grant_type", GRANT_TYPE);
                    //map.put("client_id", BuildConfigure.CLIENT_ID);
                    //map.put("client_secret", BuildConfigure.CLIENT_SECRET);
                    map.put("device_type", "android");
                    map.put("device_id", device_UDID);
                    map.put("device_token", device_token);
                    login(map);
                }
                break;

            case R.id.donnot_have_account:
                startActivity(new Intent(Login.this, SignUpActivity.class));
                break;
            case R.id.et_current_password_eye_img:
                if (etCurrentPasswordEyeImg.getTag().equals(1)) {
                    etCurrentPassword.setTransformationMethod(null);
                    etCurrentPasswordEyeImg.setImageResource(R.drawable.ic_eye_close);
                    etCurrentPasswordEyeImg.setTag(0);
                } else {
                    etCurrentPassword.setTransformationMethod(new PasswordTransformationMethod());
                    etCurrentPasswordEyeImg.setImageResource(R.drawable.ic_eye_open);
                    etCurrentPasswordEyeImg.setTag(1);
                }
                break;
            default:
                break;
        }
    }

    private void login(HashMap<String, String> map) {
        if (customDialog != null)
            customDialog.show();

        Call<Profile> call = apiInterface.login(map);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(@NonNull Call<Profile> call, @NonNull Response<Profile> response) {
                customDialog.cancel();
                if (response.isSuccessful()) {
                    if (response.body().getAccess_token() != null) {
                        GlobalData.profile = response.body();
                        SharedHelper.putKey(Login.this, "token_type",
                                "Bearer");
                        SharedHelper.putKey(Login.this, "access_token",
                                GlobalData.profile.getAccess_token());
                        //System.out.println("login " + GlobalData.token.getTokenType() + " " + GlobalData.token.getAccessToken());
                        getProfile();
                    }
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(Login.this, error.getError(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Profile> call, @NonNull Throwable t) {
                customDialog.cancel();
                Toast.makeText(Login.this, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
            }
        });


    }

    private void getProfile() {
        if (customDialog != null)
            customDialog.show();

        HashMap<String, String> map = new HashMap<>();
        map.put("device_type", "android");
        map.put("device_id", SharedHelper.getKey(this, "device_id"));
        map.put("device_token", SharedHelper.getKey(this, "device_token"));

        String header =
                SharedHelper.getKey(Login.this, "token_type") + " " + SharedHelper.getKey(Login.this, "access_token");
        System.out.println("getProfile Header " + header);
        Call<Profile> call = apiInterface.getProfile(header, map);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(@NonNull Call<Profile> call,
                                   @NonNull Response<Profile> response) {
                customDialog.cancel();
                if (response.isSuccessful()) {
                    GlobalData.profile = response.body();
                    SharedHelper.putKey(Login.this, "logged_in", "1");
                    startActivity(new Intent(Login.this, Home.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(Login.this, error.getError(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<Profile> call, @NonNull Throwable t) {
                customDialog.cancel();
                Toast.makeText(Login.this, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case ASK_MULTIPLE_PERMISSION_REQUEST_CODE:
                break;
        }
    }


    public void getDeviceToken() {
        try {
            if (!SharedHelper.getKey(Login.this, "device_token").equals("") && SharedHelper.getKey(Login.this, "device_token") != null) {
                device_token = SharedHelper.getKey(Login.this, "device_token");
                Log.d(TAG, "GCM Registration Token: " + device_token);
            } else {
                device_token = "" + FirebaseInstanceId.getInstance().getToken();
                SharedHelper.putKey(Login.this, "device_token", "" + FirebaseInstanceId.getInstance().getToken());
                Log.d(TAG, "Failed to complete token refresh: " + device_token);
            }
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            Log.d(TAG, "Failed to complete token refresh");
        }

        try {
            device_UDID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            Log.d(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            Log.d(TAG, "Failed to complete device UDID");
        }
    }

}
