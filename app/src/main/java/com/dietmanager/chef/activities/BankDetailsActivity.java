package com.dietmanager.chef.activities;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import com.dietmanager.chef.BuildConfigure;
import com.dietmanager.chef.R;
import com.dietmanager.chef.api.ApiClient;
import com.dietmanager.chef.api.ApiInterface;
import com.dietmanager.chef.helper.AppConstants;
import com.dietmanager.chef.helper.CustomDialog;
import com.dietmanager.chef.helper.SharedHelper;
import com.dietmanager.chef.model.StripeResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BankDetailsActivity extends AppCompatActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.webview)
    WebView webView;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;

    private CustomDialog customDialog;
    private boolean isUpdating = false;
    private String tokenUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_details);
        ButterKnife.bind(this);

        title.setText(getResources().getString(R.string.bank_details));
        customDialog = new CustomDialog(this);
        String stripeUrl = SharedHelper.getKey(BankDetailsActivity.this, AppConstants.STRIPE_URL);
        if (!TextUtils.isEmpty(stripeUrl)) {
            tvNoData.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            loadWebView(stripeUrl);
        } else {
            tvNoData.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
        }
    }

    private void loadWebView(String url) {
        if (customDialog != null && !customDialog.isShowing()) {
            customDialog.show();
        }
        webView.clearCache(true);
        webView.clearFormData();
        webView.clearHistory();
        webView.clearSslPreferences();

        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (customDialog != null && customDialog.isShowing()) {
                    customDialog.dismiss();
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.e("Params Url:", "" + request.getUrl());
                if (request.getUrl().toString().contains(BuildConfigure.BASE_URL+"chef/stripe/callback?code=")) {
                    tokenUrl = request.getUrl().toString();
                }
                if (!TextUtils.isEmpty(tokenUrl) & !isUpdating) {
                    isUpdating = true;
                    String tempUrl = tokenUrl;
                    String code = tempUrl.replace(BuildConfigure.BASE_URL+"chef/stripe/callback?code=", "");
                    Log.e("Final Url", code);
                    updateBankDetails(code);
                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Toast.makeText(BankDetailsActivity.this, "Something wrong with the document url", Toast.LENGTH_SHORT).show();
            }
        });
        webView.loadUrl(url);
    }

    private void updateBankDetails(String token) {
        if (customDialog != null && !customDialog.isShowing()) {
            customDialog.show();
        }
        String header = SharedHelper.getKey(BankDetailsActivity.this, "token_type") + " " + SharedHelper.getKey(BankDetailsActivity.this, "access_token");
        ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        Call<StripeResponse> call = apiInterface.updateBankDetails(header, "chef", token);
        call.enqueue(new Callback<StripeResponse>() {
            @Override
            public void onResponse(Call<StripeResponse> call, Response<StripeResponse> response) {
                if (customDialog != null && customDialog.isShowing()) {
                    customDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    String message = response.body() != null && !TextUtils.isEmpty(response.body().getMessage()) ?
                            response.body().getMessage() : "Bank Details updated successfully";
                    Toast.makeText(BankDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(BankDetailsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StripeResponse> call, Throwable t) {
                Toast.makeText(BankDetailsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.back)
    public void onClick(View view) {
        onBackPressed();
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
}
