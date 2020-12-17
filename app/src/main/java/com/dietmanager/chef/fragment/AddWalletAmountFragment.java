package com.dietmanager.chef.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.dietmanager.chef.R;
import com.dietmanager.chef.activities.BankDetailsActivity;
import com.dietmanager.chef.activities.Login;
import com.dietmanager.chef.api.APIError;
import com.dietmanager.chef.api.ApiClient;
import com.dietmanager.chef.api.ApiInterface;
import com.dietmanager.chef.api.ErrorUtils;
import com.dietmanager.chef.helper.CustomDialog;
import com.dietmanager.chef.helper.GlobalData;
import com.dietmanager.chef.helper.SharedHelper;
import com.dietmanager.chef.model.MessageResponse;
import com.dietmanager.chef.model.Profile;
import com.dietmanager.chef.model.wallet.wallet.RequestAmountResponse;
import com.dietmanager.chef.utils.Utils;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddWalletAmountFragment extends BaseFragment {

    private static final String AMOUNT_FIFTY = "50";
    private static final String AMOUNT_HUNDRED = "100";
    private static final String AMOUNT_THOUSAND = "1000";

    @BindView(R.id.edt_amount)
    EditText edtAmount;

    @BindView(R.id.etComment)
    EditText etComment;

    @BindView(R.id.wallet_amount_txt)
    TextView walletAmountTxt;
    Double walletMoney = 0.0;
    private Context context;
    private Activity activity;
    String device_token, device_UDID;
    private CustomDialog customDialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
    }



    public void getDeviceToken() {
        String TAG = "FCM";
        try {
            if (!SharedHelper.getKey(context, "device_token").equals("") && SharedHelper.getKey(context
                    , "device_token") != null) {
                device_token = SharedHelper.getKey(context, "device_token");
                Log.d(TAG, "GCM Registration Token: " + device_token);
            } else {
                device_token = FirebaseInstanceId.getInstance().getToken();
                SharedHelper.putKey(context, "device_token",
                        "" + FirebaseInstanceId.getInstance().getToken());
                Log.d(TAG, "Failed to complete token refresh: " + device_token);
            }
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            Log.d(TAG, "Failed to complete token refresh");
        }

        try {
            device_UDID = android.provider.Settings.Secure.getString(context.getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            Log.d(TAG, "Device UDID:" + device_UDID);
            SharedHelper.putKey(context, "device_id", "" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            Log.d(TAG, "Failed to complete device UDID");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customDialog = new CustomDialog(getContext());
        getDeviceToken();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_request_amount, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }
    @Override
    public void onResume() {
        super.onResume();
        getProfile();
    }

    private void getProfile() {
        HashMap<String, String> map = new HashMap<>();
        map.put("device_type", "android");
        map.put("device_id", device_UDID);
        map.put("device_token", device_token);
        String header = SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context,
                "access_token");
        System.out.println("getProfile Header " + header);
        ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);

        Call<Profile> call = apiInterface.getProfile(header,map);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(@NonNull Call<Profile> call,
                                   @NonNull Response<Profile> response) {
                if (response.isSuccessful()) {
                    GlobalData.profile = response.body();

                    if (GlobalData.profile.getWalletBalance() != null) {
                        walletMoney = GlobalData.profile.getWalletBalance();
                    }
                    walletAmountTxt.setText(GlobalData.profile.getCurrency() + " " + String.valueOf(walletMoney));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Profile> call, @NonNull Throwable t) {
                Toast.makeText(context, R.string.something_went_wrong,
                        Toast.LENGTH_LONG).show();
            }
        });
    }


    @OnClick({R.id.btn_fifty, R.id.btn_hundred, R.id.btn_thousand, R.id.btn_add_amount})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_fifty:
                requestAmount(AMOUNT_FIFTY);
                break;
            case R.id.btn_hundred:
                requestAmount(AMOUNT_HUNDRED);
                break;
            case R.id.btn_thousand:
                requestAmount(AMOUNT_THOUSAND);
                break;
            case R.id.btn_add_amount:
                if (TextUtils.isEmpty(edtAmount.getText().toString())) {
                    Utils.showToast(getContext(), "Please enter amount...");
                    return;
                }

                requestAmount(edtAmount.getText().toString());
                break;
        }
    }
    private void showLoading() {
        if (customDialog != null && !customDialog.isShowing()) {
            customDialog.show();
        }
    }

    private void hideLoading() {
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.dismiss();
        }
    }

    private void showDialog() {
        AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_yes_no_dialog, null);
        TextView yes = dialogView.findViewById(R.id.tvYes);
        TextView no = dialogView.findViewById(R.id.tvNo);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.cancel();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity, BankDetailsActivity.class));
                dialogBuilder.cancel();
            }
        });
        dialogBuilder.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();

    }
    private void requestAmount(String amount) {
        if(GlobalData.profile.getStripe_cust_id()==null){
            showDialog();
            return;
        }
        else if(etComment.getText().toString().isEmpty()){
            Utils.showToast(context, "Please enter comment");
            return;
        }
        else if (Double.parseDouble(amount)>walletMoney) {
            Utils.showToast(context, "Please choose less than wallet amount");
            return;
        }
        showLoading();
        String header = SharedHelper.getKey(getContext(), "token_type") + " " + SharedHelper.getKey(getContext(), "access_token");
        ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        Call<MessageResponse> call = apiInterface.requestAmount(header, amount,etComment.getText().toString());
        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call,
                                   @NonNull Response<MessageResponse> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    edtAmount.setText("");
                    etComment.setText("");
                    Utils.showToast(getContext(), "Amount requested");
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    String message = error != null && !TextUtils.isEmpty(error.getError()) ? error.getError() : "You cannot request amount at this time";
                    Utils.showToast(getContext(), message);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                hideLoading();
                Toast.makeText(getContext(), R.string.something_went_wrong,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
