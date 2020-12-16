package com.dietmanager.chef.model.wallet.transaction;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TransactionResponse {

    @SerializedName("wallet_requests")
    private List<TransactionItem> transactionItemList;

    public List<TransactionItem> getTransactionItemList() {
        return transactionItemList;
    }

    public void setTransactionItemList(List<TransactionItem> transactionItemList) {
        this.transactionItemList = transactionItemList;
    }
}
