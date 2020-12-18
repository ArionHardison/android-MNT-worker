package com.dietmanager.chef.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.dietmanager.chef.helper.AppConstants;
import com.dietmanager.chef.utils.Utils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.dietmanager.chef.R;
import com.dietmanager.chef.api.APIError;
import com.dietmanager.chef.api.ApiClient;
import com.dietmanager.chef.api.ApiInterface;
import com.dietmanager.chef.api.ErrorUtils;
import com.dietmanager.chef.helper.CustomDialog;
import com.dietmanager.chef.helper.GlobalData;
import com.dietmanager.chef.helper.SharedHelper;
import com.dietmanager.chef.model.Profile;
import com.dietmanager.chef.model.Token;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.philio.pinentry.PinEntryView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTP extends AppCompatActivity {

    @BindView(R.id.phone)
    TextView phone;
    @BindView(R.id.otp)
    PinEntryView otp;
    SmsVerifyCatcher smsVerifyCatcher;
    boolean isSignUp = false;

    CustomDialog customDialog;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");
                System.out.println("BroadcastReceiver" + message);
//                otp.setText(message);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        ButterKnife.bind(this);
        getDeviceToken();
        customDialog = new CustomDialog(this);
        Bundle bundle = getIntent().getExtras();


        phone.setText(SharedHelper.getKey(this, "mobile_number"));
        if (bundle != null) {
            isSignUp = bundle.getBoolean("signup", false);
            if (isSignUp){
                phone.setText(GlobalData.mobile);
                otp.setText(String.valueOf(GlobalData.otpValue));
            }
        }

        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                String code = parseCode(message);
                otp.setText(code);
            }
        });
    }

    private String parseCode(String message) {
        Pattern p = Pattern.compile("(\\d{6})");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }

    private void login(HashMap<String, String> map) {
        if (customDialog != null)
            customDialog.show();

        Call<Token> call = apiInterface.postLogin(map);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(@NonNull Call<Token> call, @NonNull Response<Token> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getAccessToken() != null) {
                    SharedHelper.putKey(OTP.this, "token_type",
                            "Bearer");
                    SharedHelper.putKey(OTP.this, "access_token",
                            response.body().getAccessToken());
                    getProfile();
                } else {
                    customDialog.cancel();
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(OTP.this, error.getError(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Token> call, @NonNull Throwable t) {
                customDialog.cancel();
                Toast.makeText(OTP.this, getString(R.string.something_went_wrong),
                        Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void getProfile() {
        if (customDialog != null && !customDialog.isShowing())
            customDialog.show();

        String header = SharedHelper.getKey(OTP.this, "token_type") + " "
                + SharedHelper.getKey(OTP.this, "access_token");

        HashMap<String, String> map = new HashMap<>();
        map.put("device_type", "android");
        map.put("device_id", SharedHelper.getKey(this, "device_id"));
        map.put("device_token", SharedHelper.getKey(this, "device_token"));

        Call<Profile> call = apiInterface.getProfile(header, map);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(@NonNull Call<Profile> call,
                                   @NonNull Response<Profile> response) {
                customDialog.cancel();
                if (response.isSuccessful()) {
                    GlobalData.profile = response.body();
                    String stripeUrl = response.body() != null && !TextUtils.isEmpty(response.body().getStripeConnectUrl()) ? response.body().getStripeConnectUrl() : "";
                    SharedHelper.putKey(OTP.this, AppConstants.STRIPE_URL, stripeUrl);
                    SharedHelper.putKey(OTP.this, "logged_in", "1");
                    launchActivity();
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(OTP.this, error.getError(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<Profile> call, @NonNull Throwable t) {
                customDialog.cancel();
                Toast.makeText(OTP.this, "Something wrong getProfile", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void launchActivity() {
        Intent intent = new Intent(getApplicationContext(), Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.continue_btn)
    public void onViewClicked() {
        String userOtp = otp.getText().toString().trim();
        if (userOtp.length() < 4) {
            Toast.makeText(this, getString(R.string.invalid_otp), Toast.LENGTH_LONG).show();
        } else {
            if (isSignUp){
                if (String.valueOf(GlobalData.otpValue).equalsIgnoreCase(userOtp)){
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else {
                    Utils.displayMessage(this, getString(R.string.wrong_otp));
                }
            }else {
                HashMap<String, String> map = new HashMap<>();
                map.put("phone", SharedHelper.getKey(this, "mobile_number"));
                map.put("otp", userOtp);
                login(map);
            }
        }
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
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
            Log.d(TAG, "COULD NOT GET FCM TOKEN");
        }

        try {
            String device_UDID = android.provider.Settings.Secure.getString(getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            SharedHelper.putKey(this, "device_id", device_UDID);
            Log.d(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "COULD NOT GET UDID");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}

