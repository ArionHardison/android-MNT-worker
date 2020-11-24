package com.dietmanager.chef.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.dietmanager.chef.CountryPicker.Country;
import com.dietmanager.chef.CountryPicker.CountryPicker;
import com.dietmanager.chef.CountryPicker.CountryPickerListener;
import com.dietmanager.chef.R;
import com.dietmanager.chef.api.ApiClient;
import com.dietmanager.chef.api.ApiInterface;
import com.dietmanager.chef.helper.AppConstants;
import com.dietmanager.chef.helper.ConnectionHelper;
import com.dietmanager.chef.helper.CustomDialog;
import com.dietmanager.chef.helper.GlobalData;
import com.dietmanager.chef.helper.SharedHelper;
import com.dietmanager.chef.model.Otp;
import com.dietmanager.chef.model.Profile;
import com.dietmanager.chef.model.RegisterResponse;
import com.dietmanager.chef.utils.TextUtils;
import com.dietmanager.chef.utils.Utils;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.email)
    EditText emailEdit;
    @BindView(R.id.name)
    EditText nameEdit;
    @BindView(R.id.password)
    EditText passwordEdit;
    @BindView(R.id.sign_up)
    Button signUpBtn;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    @BindView(R.id.app_logo)
    ImageView appLogo;

    String name, email, password, strConfirmPassword;
    String GRANT_TYPE = "password";
    Context context;
    @BindView(R.id.confirm_password)
    EditText confirmPassword;
    CustomDialog customDialog;
    @BindView(R.id.back_img)
    ImageView backImg;
    @BindView(R.id.password_eye_img)
    ImageView passwordEyeImg;
    @BindView(R.id.confirm_password_eye_img)
    ImageView confirmPasswordEyeImg;
    ConnectionHelper connectionHelper;
    Activity activity;

    String device_token, device_UDID;
    Utils utils = new Utils();
    String TAG = "Login";
    @BindView(R.id.countryImage)
    ImageView countryImage;
    @BindView(R.id.countryNumber)
    TextView countryNumber;
    @BindView(R.id.et_mobile_number)
    EditText etMobileNumber;
    @BindView(R.id.mobile_number_layout)
    RelativeLayout mobileNumberLayout;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;
    @BindView(R.id.password_layout)
    RelativeLayout passwordLayout;
    @BindView(R.id.confirm_password_layout)
    RelativeLayout confirmPasswordLayout;
    private CountryPicker mCountryPicker;
    String country_code = "+91";
    private static final int REQUEST_LOCATION = 1450;
    GoogleApiClient mGoogleApiClient;
    private String hashcode = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_sign_up);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);
        context = SignUpActivity.this;
        activity = SignUpActivity.this;
        connectionHelper = new ConnectionHelper(context);
        customDialog = new CustomDialog(context);
        mCountryPicker = CountryPicker.newInstance(getResources().getString(R.string.select_country));
        passwordEyeImg.setTag(1);
        confirmPasswordEyeImg.setTag(1);
/*        if (!GlobalData.loginBy.equals("manual")) {
            confirmPasswordLayout.setVisibility(View.GONE);
            passwordLayout.setVisibility(View.GONE);
            //mobileNumberLayout.setVisibility(View.VISIBLE);
            nameEdit.setText(GlobalData.name);
            emailEdit.setText(GlobalData.email);
        } else {
            confirmPasswordLayout.setVisibility(View.VISIBLE);
            passwordLayout.setVisibility(View.VISIBLE);
            //mobileNumberLayout.setVisibility(View.GONE);
        }*/
/*// OTP REtriver
        List<String> list = new AppSignatureHelper(this).getAppSignatures();
        Log.d(TAG, "HASH " + list.toString());
        hashcode = list.get(0);
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        Task<Void> task = client.startSmsRetriever();
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Successfully started retriever");
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                Log.d(TAG, " started retriever failed");
            }
        });*/

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

        /*----------------Face Integration---------------*/
        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = getPackageManager().getPackageInfo(
                    "com.foodie.user",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException ignored) {

        }
        getDeviceToken();

    }

    private void setListener() {
        mCountryPicker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(Country country) {
                countryNumber.setText(country.getDialCode());
                country_code = country.getDialCode();
                countryImage.setImageResource(country.getFlag());
                mCountryPicker.dismiss();
            }
        });
        countryNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });
        countryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });
        getUserCountryInfo();
    }

    private void getUserCountryInfo() {
        Country country = Country.getCountryByName("United States");
        if (country != null) {
            countryImage.setImageResource(country.getFlag());
            countryNumber.setText(country.getDialCode());
            country_code = country.getDialCode();
        } else {
            countryImage.setImageResource(R.drawable.flag_us);
            countryNumber.setText("US");
            country_code = "+1";
        }
    }

    public void getDeviceToken() {
        try {
            if (!SharedHelper.getKey(context, "device_token").equals("") && SharedHelper.getKey(context, "device_token") != null) {
                device_token = SharedHelper.getKey(context, "device_token");
                Log.d(TAG, "GCM Registration Token: " + device_token);
            } else {
                device_token = "" + FirebaseInstanceId.getInstance().getToken();
                SharedHelper.putKey(context, "device_token", "" + FirebaseInstanceId.getInstance().getToken());
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

    @OnClick({R.id.sign_up, R.id.back_img, R.id.password_eye_img, R.id.confirm_password_eye_img})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sign_up:
                initValues();
                break;
            case R.id.back_img:
                onBackPressed();
                break;
            case R.id.password_eye_img:
                if (passwordEyeImg.getTag().equals(1)) {
                    passwordEdit.setTransformationMethod(null);
                    passwordEyeImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_eye_close));
                    passwordEyeImg.setTag(0);
                } else {
                    passwordEyeImg.setTag(1);
                    passwordEdit.setTransformationMethod(new PasswordTransformationMethod());
                    passwordEyeImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_eye_open));
                }
                break;
            case R.id.confirm_password_eye_img:
                if (confirmPasswordEyeImg.getTag().equals(1)) {
                    confirmPassword.setTransformationMethod(null);
                    confirmPasswordEyeImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_eye_close));
                    confirmPasswordEyeImg.setTag(0);
                } else {
                    confirmPasswordEyeImg.setTag(1);
                    confirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                    confirmPasswordEyeImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_eye_open));
                }
                break;
        }
    }


    public void signup(HashMap<String, String> map) {
        customDialog.show();
        Call<RegisterResponse> call = apiInterface.postRegister(map);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(@NonNull Call<RegisterResponse> call, @NonNull Response<RegisterResponse> response) {
                if (response.body() != null) {
                    HashMap<String, String> map = new HashMap<>();
                   /* map.put("mobile",etMobileNumber.getText().toString());
                    map.put("dial_code",country_code);*/
                    map.put("username",email);
                    map.put("password", password);
                    //map.put("grant_type", "password");
                    //map.put("client_id", AppConfigure.CLIENT_ID);
                    //map.put("client_secret", AppConfigure.CLIENT_SECRET);
                    //map.put("guard", "shops");
                    map.put("device_id", device_UDID);
                    map.put("device_token", device_token);
                    map.put("device_type", AppConstants.DEVICE_TYPE);
                    login(map);
                } else if (response.errorBody() != null) {
                    customDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        if (jObjError.has("email"))
                            Toast.makeText(context, jObjError.optJSONArray("email").get(0).toString(), Toast.LENGTH_LONG).show();
                        else if (jObjError.has("phone"))
                            Toast.makeText(context, jObjError.optJSONArray("phone").get(0).toString(), Toast.LENGTH_LONG).show();
                        else if (jObjError.has("password"))
                            Toast.makeText(context, jObjError.optJSONArray("password").get(0).toString(), Toast.LENGTH_LONG).show();
                        else if (jObjError.has("error"))
                            Toast.makeText(context, jObjError.optJSONArray("error").get(0).toString(), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(context, "Invalid", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RegisterResponse> call, @NonNull Throwable t) {

            }
        });

    }


    private void login(HashMap<String, String> map) {
        Call<Profile> call = apiInterface.login(map);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(@NonNull Call<Profile> call, @NonNull Response<Profile> response) {
                if (response.body() != null) {
                    SharedHelper.putKey(context, "access_token", response.body().getAccess_token());
                    GlobalData.accessToken = response.body().getAccess_token();
                    //Get Profile data
                    getProfile();

                }

            }

            @Override
            public void onFailure(@NonNull Call<Profile> call, @NonNull Throwable t) {

            }
        });
    }

    public void initValues() {
        name = nameEdit.getText().toString();
        email = emailEdit.getText().toString();
        strConfirmPassword = confirmPassword.getText().toString();
        GlobalData.mobile = country_code + etMobileNumber.getText().toString();
        password = passwordEdit.getText().toString();
        if (com.dietmanager.chef.utils.TextUtils.isEmpty(name)) {
            Toast.makeText(this, getResources().getString(R.string.please_enter_username), Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, getResources().getString(R.string.please_enter_your_email), Toast.LENGTH_SHORT).show();
        } else if (!TextUtils.isValidEmail(email)) {
            Toast.makeText(this, getResources().getString(R.string.please_enter_valid_email), Toast.LENGTH_SHORT).show();
        } else if (!isValidMobile(etMobileNumber.getText().toString()) ) {
            Toast.makeText(this, getResources().getString(R.string.please_enter_your_mobile_number), Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password) ) {
            Toast.makeText(this, getResources().getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(strConfirmPassword)) {
            Toast.makeText(this, getResources().getString(R.string.please_enter_your_confirm_password), Toast.LENGTH_SHORT).show();
        } else if (!strConfirmPassword.equalsIgnoreCase(password) ) {
            Toast.makeText(this, getResources().getString(R.string.password_and_confirm_password_doesnot_match), Toast.LENGTH_SHORT).show();
        } else {

                    HashMap<String, String> map1 = new HashMap<>();
                    map1.put("dial_code",country_code );
            map1.put("mobile", etMobileNumber.getText().toString());
                    //map1.put("login_by", GlobalData.loginBy);
                    //map1.put("accessToken", GlobalData.access_token);
                    getOtpVerification(map1);
        }
    }


    private void signUp(){
        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("email", email);
        map.put("mobile", etMobileNumber.getText().toString());
        map.put("password", password);
        map.put("password_confirmation", strConfirmPassword);
        map.put("dial_code",country_code);
        map.put("device_id", device_UDID);
        map.put("device_token", device_token);
        map.put("device_type", AppConstants.DEVICE_TYPE);
        signup(map);
    }

    public void getOtpVerification(HashMap<String, String> map) {
        customDialog.show();
        Call<Otp> call = apiInterface.postOtp(map);
        call.enqueue(new Callback<Otp>() {
            @Override
            public void onResponse(@NonNull Call<Otp> call, @NonNull Response<Otp> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(context, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    GlobalData.otpValue = response.body().getOtp();
                    Intent intent = new Intent(SignUpActivity.this, OTP.class);
                    intent.putExtra("signup", true);
                    startActivityForResult(intent, 111);
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        if (jObjError.has("phone"))
                            Toast.makeText(context, jObjError.optString("phone"), Toast.LENGTH_LONG).show();
                        else if (jObjError.has("email"))
                            Toast.makeText(context, jObjError.optString("email"), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<Otp> call, @NonNull Throwable t) {
                customDialog.dismiss();
                Toast.makeText(SignUpActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getProfile() {
        HashMap<String, String> map = new HashMap<>();
        map.put("device_type", "android");
        map.put("device_id", device_UDID);
        map.put("device_token", device_token);
        String header = SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token");

        Call<Profile> getprofile = apiInterface.getProfile(header,map);
        getprofile.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(@NonNull Call<Profile> call, @NonNull Response<Profile> response) {
                if (response.isSuccessful()) {
                    SharedHelper.putKey(context, "logged", "true");
                    GlobalData.profile = response.body();
                    startActivity(new Intent(context, Home.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                } else {
                    if (response.code() == 401) {
                        Toast.makeText(context, "UnAuthenticated", Toast.LENGTH_LONG).show();
                        SharedHelper.putKey(context, "logged", "false");
                        startActivity(new Intent(context, Login.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    }

                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().toString());
                        Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<Profile> call, @NonNull Throwable t) {

            }
        });
    }

   /* @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 111) {
            signUp();
        }
    }

    private boolean isValidMobile(String phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
//            if( phone.length() > 10) {
            check = phone.length() == 10;
        } else {
            check = false;
        }
        return check;
    }
}