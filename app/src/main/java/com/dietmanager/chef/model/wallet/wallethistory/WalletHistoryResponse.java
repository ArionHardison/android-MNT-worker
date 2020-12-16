package com.dietmanager.chef.model.wallet.wallethistory;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WalletHistoryResponse {

    @SerializedName("refundHistories")
    private List<WalletItem> walletItemList;

    public List<WalletItem> getWalletItemList() {
        return walletItemList;
    }

    public void setWalletItemList(List<WalletItem> walletItemList) {
        this.walletItemList = walletItemList;
    }
}
