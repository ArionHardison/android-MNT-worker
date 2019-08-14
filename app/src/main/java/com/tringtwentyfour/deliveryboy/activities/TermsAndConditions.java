package com.tringtwentyfour.deliveryboy.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.tringtwentyfour.deliveryboy.R;
import com.tringtwentyfour.deliveryboy.helper.GlobalData;
import com.tringtwentyfour.deliveryboy.helper.LocaleUtils;
import com.tringtwentyfour.deliveryboy.helper.SharedHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.tringtwentyfour.deliveryboy.BuildConfigure.BASE_URL;

public class TermsAndConditions extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.terms_condtion)
    TextView terms_condtion;
    @BindView(R.id.webView)
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);
        ButterKnife.bind(this);

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


        title.setText(getResources().getString(R.string.terms_and_conditions));
        terms_condtion.setText(GlobalData.profile.getTerms());
        webView.loadData(GlobalData.profile.getTerms(), "text/html", "UTF-8");
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(BASE_URL+"terms");

    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        onBackPressed();
    }



    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
