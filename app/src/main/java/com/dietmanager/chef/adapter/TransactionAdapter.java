package com.dietmanager.chef.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.dietmanager.chef.R;
import com.dietmanager.chef.model.wallet.transaction.TransactionItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<TransactionItem> transactionItemList;

    public TransactionAdapter() {
        transactionItemList = new ArrayList<>();
    }

    public void setTransactionItemList(List<TransactionItem> itemList) {
        if (itemList == null) {
            return;
        }
        transactionItemList.clear();
        transactionItemList.addAll(itemList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transactions, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        TransactionItem item = transactionItemList.get(position);
        holder.bindDataToViews(item);
    }

    @Override
    public int getItemCount() {
        return transactionItemList.size();
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_trans_id)
        TextView tvTransactionId;
        @BindView(R.id.tv_amount)
        TextView tvAmount;
        @BindView(R.id.tv_status)
        TextView tvStatus;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindDataToViews(TransactionItem item) {
            tvTransactionId.setText(String.valueOf(item.getId()));
            tvAmount.setText(!TextUtils.isEmpty(item.getAmount()) ? "$" + item.getAmount() : "");
            tvStatus.setText(!TextUtils.isEmpty(item.getStatus()) ? item.getStatus() : "NA");
        }
    }

}
