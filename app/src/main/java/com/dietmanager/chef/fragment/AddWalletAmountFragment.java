package com.dietmanager.chef.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dietmanager.chef.R;
import com.dietmanager.chef.api.APIError;
import com.dietmanager.chef.api.ApiClient;
import com.dietmanager.chef.api.ApiInterface;
import com.dietmanager.chef.api.ErrorUtils;
import com.dietmanager.chef.helper.CustomDialog;
import com.dietmanager.chef.helper.SharedHelper;
import com.dietmanager.chef.model.MessageResponse;
import com.dietmanager.chef.model.wallet.wallet.RequestAmountResponse;
import com.dietmanager.chef.utils.Utils;

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

    private CustomDialog customDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customDialog = new CustomDialog(getContext());
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
    private void requestAmount(String amount) {
        showLoading();
        String header = SharedHelper.getKey(getContext(), "token_type") + " " + SharedHelper.getKey(getContext(), "access_token");
        ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        Call<MessageResponse> call = apiInterface.requestAmount(header, amount);
        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call,
                                   @NonNull Response<MessageResponse> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    edtAmount.setText("");
                    Utils.showToast(getContext(), "Amount requested...");
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    String message = error != null && !TextUtils.isEmpty(error.getError()) ? error.getError() : "You cannot request amount at this time...";
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
