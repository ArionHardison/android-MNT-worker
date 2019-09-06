package com.oyola.deliveryboy.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oyola.deliveryboy.R;
import com.oyola.deliveryboy.adapter.NoticeAdapter;
import com.oyola.deliveryboy.api.APIError;
import com.oyola.deliveryboy.api.ErrorUtils;
import com.oyola.deliveryboy.helper.ConnectionHelper;
import com.oyola.deliveryboy.helper.CustomDialog;
import com.oyola.deliveryboy.helper.GlobalData;
import com.oyola.deliveryboy.helper.SharedHelper;
import com.oyola.deliveryboy.model.Notice;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NoticeBoard extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;

    CustomDialog customDialog;
    ConnectionHelper connectionHelper;
    @BindView(R.id.notice_rv)
    RecyclerView noticeRv;
    List<Notice> list;
    @BindView(R.id.error_layout)
    LinearLayout errorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board);
        ButterKnife.bind(this);
        title.setText(getResources().getString(R.string.notice_board));
        customDialog = new CustomDialog(this);
        connectionHelper = new ConnectionHelper(this);

        list = new ArrayList<>();
        NoticeAdapter adapter = new NoticeAdapter(list, this);
        noticeRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        noticeRv.setItemAnimator(new DefaultItemAnimator());
        noticeRv.setAdapter(adapter);
        getNoticeBoard();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getNoticeBoard() {
        if (!connectionHelper.isConnectingToInternet()) {
            Snackbar snackbar = Snackbar.make(this.findViewById(android.R.id.content), getResources().getString(R.string.check_your_internet_connection), Snackbar.LENGTH_SHORT);
            snackbar.show();
            return;
        }

        customDialog.show();

        String header = SharedHelper.getKey(NoticeBoard.this, "token_type") + " " + SharedHelper.getKey(NoticeBoard.this, "access_token");
        Call<List<Notice>> call = GlobalData.api.getNoticeBoard(header);
        call.enqueue(new Callback<List<Notice>>() {
            @Override
            public void onResponse(@NonNull Call<List<Notice>> call, @NonNull Response<List<Notice>> response) {
                customDialog.cancel();
                if (response.isSuccessful()) {
                    if (response.body().size() > 0) {
                        list.clear();
                        list.addAll(response.body());
                        errorLayout.setVisibility(View.GONE);
                        noticeRv.getAdapter().notifyDataSetChanged();
                    } else {
                        list.clear();
                        noticeRv.getAdapter().notifyDataSetChanged();
                        errorLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(NoticeBoard.this, error.getError(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Notice>> call, @NonNull Throwable t) {
                customDialog.cancel();
                Toast.makeText(NoticeBoard.this, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        onBackPressed();
    }
}
