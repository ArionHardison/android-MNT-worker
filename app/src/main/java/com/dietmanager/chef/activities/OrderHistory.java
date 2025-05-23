package com.dietmanager.chef.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.android.material.snackbar.Snackbar;
import com.dietmanager.chef.R;
import com.dietmanager.chef.adapter.OrderHistoryAdapter;
import com.dietmanager.chef.api.APIError;
import com.dietmanager.chef.api.ErrorUtils;
import com.dietmanager.chef.helper.ConnectionHelper;
import com.dietmanager.chef.helper.CustomDialog;
import com.dietmanager.chef.helper.GlobalData;
import com.dietmanager.chef.helper.SharedHelper;
import com.dietmanager.chef.model.Order;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistory extends AppCompatActivity {
    OrderHistoryAdapter orderHistoryAdapter;
    List<Order> orders;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.orders_rv)
    RecyclerView ordersRv;
    ConnectionHelper connectionHelper;
    @BindView(R.id.history_filter)
    Spinner historyFilter;
    @BindView(R.id.error_layout)
    LinearLayout errorLayout;
    CustomDialog customDialog;
    @BindView(R.id.root_view)
    RelativeLayout rootView;

    private SkeletonScreen skeletonScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        ButterKnife.bind(this);
        connectionHelper = new ConnectionHelper(this);
        title.setText(getResources().getString(R.string.order_history));
        customDialog = new CustomDialog(this);

        orders = new ArrayList<>();
        orderHistoryAdapter = new OrderHistoryAdapter(orders, this);
        ordersRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ordersRv.setItemAnimator(new DefaultItemAnimator());
        ordersRv.setAdapter(orderHistoryAdapter);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.history_filter_category, R.layout.category_spinner_item);
        historyFilter.setAdapter(adapter);
        historyFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getOrdersHistory(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void getOrdersHistory(Integer value) {
        Calendar calendar = Calendar.getInstance();

        switch (value) {
            case 0: //today
                getOrdersHistory("today");
                break;
            case 1: //this week
                getOrdersHistory("weekly");
                break;
            case 2: //this month
                getOrdersHistory("monthly");
                break;
            default:
                getOrdersHistory("today");
                break;
        }
    }

    private void getOrdersHistory(String type) {

        if (!connectionHelper.isConnectingToInternet()) {
            Snackbar snackbar = Snackbar.make(this.findViewById(android.R.id.content), getResources().getString(R.string.check_your_internet_connection), Snackbar.LENGTH_SHORT);
            snackbar.show();
            return;
        }

        skeletonScreen = Skeleton.bind(rootView)
                .load(R.layout.skeleton_order)
                .angle(0)
                .show();

        String header = SharedHelper.getKey(this, "token_type") + " " + SharedHelper.getKey(this, "access_token");
        Call<List<Order>> call = GlobalData.api.getCompletedOrder(header, type);
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(@NonNull Call<List<Order>> call, @NonNull Response<List<Order>> response) {
                skeletonScreen.hide();
                Log.d("getOrdersHistory@@@", response.toString());
                if (response.isSuccessful()) {
                    if (response.body().size() > 0) {
                        orders.clear();
                        orders.addAll(response.body());
                        errorLayout.setVisibility(View.GONE);
                        orderHistoryAdapter.notifyDataSetChanged();
                    } else {
                        errorLayout.setVisibility(View.VISIBLE);
                        orders.clear();
                        orderHistoryAdapter.notifyDataSetChanged();
                    }
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(OrderHistory.this, error.getError(), Toast.LENGTH_SHORT).show();
                    if (response.code() == 401) {
                        SharedHelper.putKey(OrderHistory.this, "logged_in", "0");
                        startActivity(new Intent(OrderHistory.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Order>> call, @NonNull Throwable t) {
                Toast.makeText(OrderHistory.this, "Something wrong - OrderHistory-> getOrdersHistory()", Toast.LENGTH_SHORT).show();
                skeletonScreen.hide();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        onBackPressed();
    }
}
