package com.dietmanager.chef.model.wallet.wallethistory;

import com.google.gson.annotations.SerializedName;

public class WalletItem {

    @SerializedName("transporter_id")
    private String transporterId;
    @SerializedName("order_id")
    private String orderId;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("amount")
    private String amount;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private String status;

    public String getTransporterId() {
        return transporterId;
    }

    public void setTransporterId(String transporterId) {
        this.transporterId = transporterId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
