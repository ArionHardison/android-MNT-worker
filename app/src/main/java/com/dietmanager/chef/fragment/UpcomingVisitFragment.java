package com.dietmanager.chef.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dietmanager.chef.R;
import com.dietmanager.chef.activities.Home;
import com.dietmanager.chef.adapter.HistoryAdapter;
import com.dietmanager.chef.api.ApiClient;
import com.dietmanager.chef.api.ApiInterface;
import com.dietmanager.chef.helper.SharedHelper;
import com.dietmanager.chef.model.orderrequest.OrderRequestItem;
import com.dietmanager.chef.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpcomingVisitFragment extends BaseFragment {

    @BindView(R.id.upcoming_rv)
    RecyclerView upcomingRv;

    @BindView(R.id.llNoRecords)
    LinearLayout llNoRecords;
    private HistoryAdapter historyAdapter;

    private Unbinder unbinder;
    private Context context;
    private Activity activity;

    private List<OrderRequestItem> orderList = new ArrayList<>();
    private ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);

    public UpcomingVisitFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (activity == null)
            activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_visit, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupAdapter();
        getHistory();
    }

    private void setupAdapter() {
        historyAdapter = new HistoryAdapter(orderList, context);
        upcomingRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        upcomingRv.setHasFixedSize(true);
        upcomingRv.setAdapter(historyAdapter);
    }

    private void getHistory() {
        Home.showDialog();
        String header = SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token");

        Call<List<OrderRequestItem>> call = apiInterface.getHistory(header,"");
        call.enqueue(new Callback<List<OrderRequestItem>>() {
            @Override
            public void onResponse(Call<List<OrderRequestItem>> call, Response<List<OrderRequestItem>> response) {
                Home.dismissDialog();
                if (response.isSuccessful()) {
                    orderList.clear();
                    List<OrderRequestItem> historyModel = response.body();
                    if (historyModel != null) {
                        if (historyModel != null && historyModel.size() > 0) {
                            llNoRecords.setVisibility(View.GONE);
                            upcomingRv.setVisibility(View.VISIBLE);
                            orderList = historyModel;
                            sortOrdersToDescending(orderList);
                            historyAdapter.setList(orderList);
                            historyAdapter.notifyDataSetChanged();
                        } else {
                            llNoRecords.setVisibility(View.VISIBLE);
                            upcomingRv.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<OrderRequestItem>> call, Throwable t) {
                Home.dismissDialog();
                Utils.displayMessage(activity, getString(R.string.something_went_wrong));
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}