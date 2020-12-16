package com.dietmanager.chef.model.wallet.wallet;

import com.google.gson.annotations.SerializedName;

public class RequestAmountItem {

    @SerializedName("id")
    private long id;
    @SerializedName("transporter_id")
    private long transporterId;
    @SerializedName("amount")
    private String amount;
    @SerializedName("status")
    private String status;
    @SerializedName("comment")
    private String comment;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTransporterId() {
        return transporterId;
    }

    public void setTransporterId(long transporterId) {
        this.transporterId = transporterId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
