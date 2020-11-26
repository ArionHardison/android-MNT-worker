package com.dietmanager.chef.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dietmanager.chef.R;
import com.dietmanager.chef.api.ApiClient;
import com.dietmanager.chef.api.ApiInterface;
import com.dietmanager.chef.helper.ConnectionHelper;
import com.dietmanager.chef.helper.CustomDialog;
import com.dietmanager.chef.model.ForgotPasswordResponse;
import com.dietmanager.chef.model.ServerError;
import com.dietmanager.chef.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dietmanager.chef.utils.TextUtils.isValidEmail;


public class ForgotPassword extends AppCompatActivity {

    @BindView(R.id.et_email)
    EditText etEmail;

    @BindView(R.id.next_btn)
    Button btnNext;


    ConnectionHelper connectionHelper;
    CustomDialog customDialog;


    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    String strEmail;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        ButterKnife.bind(this);

        setUp();
    }

    private void setUp() {
        connectionHelper = new ConnectionHelper(this);
        customDialog = new CustomDialog(this);
    }

    @OnClick({R.id.next_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.next_btn:
                if (validateInput()) {
                    if (connectionHelper.isConnectingToInternet()) {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("email", strEmail);
                        getOTP(params);
                    } else
                        Utils.displayMessage(this, getString(R.string.oops_no_internet));
                }
                break;

        }
    }

    private void getOTP(HashMap<String, String> map) {
        customDialog.show();
        Call<ForgotPasswordResponse> call = apiInterface.forgotPassword(map);
        call.enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getMessage() != null) {
                        redirectToOtpScreen(response.body().getMessage());
                    } else if (response.body().getError() != null)
                        Utils.displayMessage(ForgotPassword.this, response.body().getError());
                } else {
                    try {
                        if (response.code() == 422) {
                            Utils.displayMessage(ForgotPassword.this, getString(R.string.invalid_email));
                        } else {
                            ServerError serverError = new Gson().fromJson(response.errorBody().charStream(), ServerError.class);
                            Utils.displayMessage(ForgotPassword.this, getString(R.string.something_went_wrong));
                        }

                    } catch (JsonSyntaxException e) {
                        Utils.displayMessage(ForgotPassword.this, getString(R.string.something_went_wrong));
                    }

                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                customDialog.dismiss();
                Utils.displayMessage(ForgotPassword.this, getString(R.string.something_went_wrong));
            }
        });
    }

    private void redirectToOtpScreen(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(title).setCancelable(false);
        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(ForgotPassword.this, Login.class);
                //intent.putExtra("email", strEmail);
                startActivity(intent);
                finish();

            }
        });
        builder.show();
    }

    private boolean validateInput() {
        strEmail = etEmail.getText().toString().trim();
        if (strEmail.isEmpty()) {
            Utils.displayMessage(this, getString(R.string.please_enter_mail_id));
            return false;
        } else if (!isValidEmail(strEmail)) {
            Utils.displayMessage(this, getResources().getString(R.string.please_enter_valid_mail_id));
            return false;
        }
        return true;
    }

}
