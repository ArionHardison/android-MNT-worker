package com.dietmanager.chef.model.wallet.wallet;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RequestAmountResponse {

    @SerializedName("earnings")
    private List<RequestAmountItem> requestAmountItemList;

    public List<RequestAmountItem> getRequestAmountItemList() {
        return requestAmountItemList;
    }

    public void setRequestAmountItemList(List<RequestAmountItem> requestAmountItemList) {
        this.requestAmountItemList = requestAmountItemList;
    }
}
