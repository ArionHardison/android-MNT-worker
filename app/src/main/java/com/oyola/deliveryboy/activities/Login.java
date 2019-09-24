package com.oyola.deliveryboy.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.oyola.deliveryboy.CountryPicker.Country;
import com.oyola.deliveryboy.CountryPicker.CountryPicker;
import com.oyola.deliveryboy.CountryPicker.CountryPickerListener;
import com.oyola.deliveryboy.R;
import com.oyola.deliveryboy.api.APIError;
import com.oyola.deliveryboy.api.ApiClient;
import com.oyola.deliveryboy.api.ApiInterface;
import com.oyola.deliveryboy.api.ErrorUtils;
import com.oyola.deliveryboy.helper.ConnectionHelper;
import com.oyola.deliveryboy.helper.CustomDialog;
import com.oyola.deliveryboy.helper.GlobalData;
import com.oyola.deliveryboy.helper.SharedHelper;
import com.oyola.deliveryboy.model.Otp;
import com.oyola.deliveryboy.model.Profile;
import com.oyola.deliveryboy.model.Token;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 0;
    CustomDialog customDialog;
    @BindView(R.id.country_image)
    ImageView countryImage;
    @BindView(R.id.country_number)
    TextView countryNumber;
    @BindView(R.id.mobile_no)
    EditText mobileNo;
    @BindView(R.id.submit)
    Button submit;
    String country_code;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    ConnectionHelper connectionHelper;
    private CountryPicker mCountryPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        getDeviceToken();
        customDialog = new CustomDialog(Login.this);
        connectionHelper = new ConnectionHelper(this);
        if (SharedHelper.getKey(Login.this, "logged_in").equalsIgnoreCase("1")) {
            startActivity(new Intent(Login.this, ShiftStatus.class));
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
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(Login.this, error.getError(), Toast.LENGTH_SHORT).show();
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
        Country country = Country.getCountrydetails("IN");
        if (country != null) {
            countryImage.setImageResource(country.getFlag());
            countryNumber.setText(country.getDialCode());
            country_code = country.getDialCode();
        } else {
            countryImage.setImageResource(R.drawable.flag_in);
            countryNumber.setText("IN");
            country_code = "+91";
        }
    }

    @OnClick({R.id.submit, R.id.tv_agree_policies})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.submit:
                String phone = mobileNo.getText().toString().trim();
                if (!TextUtils.isEmpty(phone)) {
                    String mobileNumber = countryNumber.getText().toString().trim() + phone;
                    SharedHelper.putKey(Login.this, "mobile_number", mobileNumber);
                    getOtpVerification(mobileNumber);
                } else {
                    Toast.makeText(Login.this, getString(R.string.please_enternumber_pswd),
                            Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.tv_agree_policies:
                startActivity(new Intent(getApplicationContext(), TermsAndConditions.class));
                break;

            default:
                break;
        }
    }

    private void login(HashMap<String, String> map) {
        if (customDialog != null)
            customDialog.show();

        Call<Token> call = apiInterface.postLogin(map);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(@NonNull Call<Token> call, @NonNull Response<Token> response) {
                customDialog.cancel();
                if (response.isSuccessful()) {
                    if (response.body().getAccessToken() != null) {
                        GlobalData.token = response.body();
                        SharedHelper.putKey(Login.this, "token_type",
                                GlobalData.token.getTokenType());
                        SharedHelper.putKey(Login.this, "access_token",
                                GlobalData.token.getAccessToken());
                        System.out.println("login " + GlobalData.token.getTokenType() + " " + GlobalData.token.getAccessToken());
                        getProfile();
                    }
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(Login.this, error.getError(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Token> call, @NonNull Throwable t) {
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
                    startActivity(new Intent(Login.this, ShiftStatus.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case ASK_MULTIPLE_PERMISSION_REQUEST_CODE:
                break;
        }
    }


    public void getDeviceToken() {
        String TAG = "FCM";
        try {
            if (!SharedHelper.getKey(this, "device_token").equals("") && SharedHelper.getKey(this
                    , "device_token") != null) {
                String device_token = SharedHelper.getKey(this, "device_token");
                Log.d(TAG, "GCM Registration Token: " + device_token);
            } else {
                String device_token = FirebaseInstanceId.getInstance().getToken();
                SharedHelper.putKey(this, "device_token",
                        "" + FirebaseInstanceId.getInstance().getToken());
                Log.d(TAG, "Failed to complete token refresh: " + device_token);
            }
        } catch (Exception e) {
            String device_token = "COULD NOT GET FCM TOKEN";
            Log.d(TAG, "Failed to complete token refresh");
        }

        try {
            String device_UDID = android.provider.Settings.Secure.getString(getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            Log.d(TAG, "Device UDID:" + device_UDID);
            SharedHelper.putKey(this, "device_id", "" + device_UDID);
        } catch (Exception e) {
            String device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            Log.d(TAG, "Failed to complete device UDID");
        }
    }
}
