package com.dietmanager.chef.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.dietmanager.chef.helper.AppConstants;
import com.google.firebase.iid.FirebaseInstanceId;
import com.dietmanager.chef.R;
import com.dietmanager.chef.api.APIError;
import com.dietmanager.chef.api.ApiClient;
import com.dietmanager.chef.api.ApiInterface;
import com.dietmanager.chef.api.ErrorUtils;
import com.dietmanager.chef.helper.ConnectionHelper;
import com.dietmanager.chef.helper.GlobalData;
import com.dietmanager.chef.helper.LocaleUtils;
import com.dietmanager.chef.helper.SharedHelper;
import com.dietmanager.chef.model.Profile;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Splash extends AppCompatActivity {
    ConnectionHelper helper;
    int retryCount = 0;
    AlertDialog alert;
    ApiInterface api = ApiClient.getRetrofit().create(ApiInterface.class);
    String device_token, device_UDID;
    Boolean isNotification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        SharedHelper.putKey(Splash.this, "token_type",
                "Bearer");
        helper = new ConnectionHelper(this);
        getDeviceToken();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value1 = extras.getString("Notification");
            if (value1 != null) {
                isNotification = true;
            }
        }
        String dd = SharedHelper.getKey(this, "language");
        switch (dd) {
            case "English":
                LocaleUtils.setLocale(this, "en");
                break;
            case "Japanese":
                LocaleUtils.setLocale(this, "ja");
                break;
            default:
                LocaleUtils.setLocale(this, "en");
                break;
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 3000ms
                if (SharedHelper.getKey(Splash.this, "logged_in").equalsIgnoreCase("1")) {
                    if (helper.isConnectingToInternet()) {
                        getProfile();
                    } else {
                        showDialog();
                    }
                } else {
                    startActivity(new Intent(Splash.this, Login.class));
                    finish();
                }

            }
        }, 3000);

    }


    private void checkActivity() {
        Intent intent = getIntent();
        if (intent.getStringExtra("page") == null || intent.getStringExtra("page").equalsIgnoreCase("main")|| intent.getStringExtra("page").equalsIgnoreCase("autoassign")) {
            startActivity(new Intent(Splash.this, Home.class).putExtra("is_splash",
                    true));
        } else {
            String page=intent.getStringExtra("page");
            if (page.equalsIgnoreCase("order")) {
                startActivity(new Intent(Splash.this, OrderRequestDetailActivity.class).putExtra("order_id",intent.getIntExtra("order_id",0))
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }  else if (page.equalsIgnoreCase("wallet")) {
                startActivity(new Intent(Splash.this, PaymentActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            } else {
                startActivity(new Intent(Splash.this, Home.class).putExtra("is_splash",
                        true));
            }
        }

        finishAffinity();
    }



    public void getProfile() {
        retryCount++;
        HashMap<String, String> map = new HashMap<>();
        map.put("device_type", "android");
        map.put("device_id", device_UDID);
        map.put("device_token", device_token);
        String header = SharedHelper.getKey(Splash.this, "token_type") + " "
                + SharedHelper.getKey(Splash.this, "access_token");
        Call<Profile> call = api.getProfile(header, map);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(@NonNull Call<Profile> call,
                                   @NonNull Response<Profile> response) {

                if (response.isSuccessful()) {
                    GlobalData.profile = response.body();
                    String stripeUrl = response.body() != null && !TextUtils.isEmpty(response.body().getStripeConnectUrl()) ? response.body().getStripeConnectUrl() : "";
                    SharedHelper.putKey(Splash.this, AppConstants.STRIPE_URL, stripeUrl);
                    SharedHelper.putKey(Splash.this, "logged_in", "1");
                    SharedHelper.putKey(Splash.this, "currency_code",
                            GlobalData.profile.getCurrency());

                    checkActivity();
                    /*//if (isNotification)
                        startActivity(new Intent(Splash.this, Home.class).putExtra("is_splash",
                                true));
                    //else
                        //startActivity(new Intent(Splash.this, ShiftStatus.class).putExtra("is_splash", true));

                    finish();*/
                } else {
                    if (response.code() == 401) {
                        SharedHelper.putKey(Splash.this, "logged_in", "0");
                        startActivity(new Intent(Splash.this, Login.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    }
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(Splash.this, error.getError(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<Profile> call, @NonNull Throwable t) {
                Toast.makeText(Splash.this, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                if (retryCount < 5) {
                    getProfile();
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.connect_to_network))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.connect_to_wifi),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                alert.dismiss();
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            }
                        })
                .setNegativeButton(getString(R.string.quit), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alert.dismiss();
                        finish();
                    }
                });
        if (alert == null) {
            alert = builder.create();
            alert.show();
        }
    }


    public void getDeviceToken() {
        String TAG = "FCM";
        try {
            if (!SharedHelper.getKey(this, "device_token").equals("") && SharedHelper.getKey(this
                    , "device_token") != null) {
                device_token = SharedHelper.getKey(this, "device_token");
                Log.d(TAG, "GCM Registration Token: " + device_token);
            } else {
                device_token = FirebaseInstanceId.getInstance().getToken();
                SharedHelper.putKey(this, "device_token",
                        "" + FirebaseInstanceId.getInstance().getToken());
                Log.d(TAG, "Failed to complete token refresh: " + device_token);
            }
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            Log.d(TAG, "Failed to complete token refresh");
        }

        try {
            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            Log.d(TAG, "Device UDID:" + device_UDID);
            SharedHelper.putKey(this, "device_id", device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            Log.d(TAG, "Failed to complete device UDID");
        }
    }
}
