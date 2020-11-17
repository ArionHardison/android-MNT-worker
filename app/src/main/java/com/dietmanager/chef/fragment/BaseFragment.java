package com.dietmanager.chef.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.dietmanager.chef.model.orderrequest.OrderRequestItem;
import java.util.Collections;
import java.util.List;

public abstract class BaseFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void sortOrdersToDescending(List<OrderRequestItem> orderList) {
        Collections.sort(orderList, (lhs, rhs) -> {
            if (rhs.getCreatedAt() == null || lhs.getCreatedAt() == null) {
                return 0;
            }
            return rhs.getCreatedAt().compareTo(lhs.getCreatedAt());
        });
    }
}
