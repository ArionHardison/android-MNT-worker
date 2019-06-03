package com.pakupaku.deliver.model;

/**
 * Created by santhosh@appoets.com on 05-10-2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Invoice {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("order_id")
    @Expose
    private Integer orderId;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("paid")
    @Expose
    private Integer paid;
    @SerializedName("gross")
    @Expose
    private int gross;
    @SerializedName("discount")
    @Expose
    private int discount;
    @SerializedName("promocode_amount")
    @Expose
    private int promocode_amount;
    @SerializedName("delivery_charge")
    @Expose
    private int deliveryCharge;
    @SerializedName("wallet_amount")
    @Expose
    private int walletAmount;
    @SerializedName("payable")
    @Expose
    private int payable;
    @SerializedName("tax")
    @Expose
    private int tax;
    @SerializedName("net")
    @Expose
    private int net;
    @SerializedName("total_pay")
    @Expose
    private int totalPay;
    @SerializedName("tender_pay")
    @Expose
    private int tenderPay;
    @SerializedName("payment_mode")
    @Expose
    private String paymentMode;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Invoice withId(Integer id) {
        this.id = id;
        return this;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Invoice withOrderId(Integer orderId) {
        this.orderId = orderId;
        return this;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Invoice withQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public Integer getPaid() {
        return paid;
    }

    public void setPaid(Integer paid) {
        this.paid = paid;
    }

    public Invoice withPaid(Integer paid) {
        this.paid = paid;
        return this;
    }

    public int getGross() {
        return gross;
    }

    public void setGross(int gross) {
        this.gross = gross;
    }

    public Invoice withGross(int gross) {
        this.gross = gross;
        return this;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getPromocode_amount() {
        return promocode_amount;
    }

    public void setPromocode_amount(int promocode_amount) {
        this.promocode_amount = promocode_amount;
    }

    public Invoice withDiscount(int discount) {
        this.discount = discount;
        return this;
    }

    public int getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(int deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public Invoice withDeliveryCharge(int deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
        return this;
    }

    public int getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(int walletAmount) {
        this.walletAmount = walletAmount;
    }

    public Invoice withWalletAmount(int walletAmount) {
        this.walletAmount = walletAmount;
        return this;
    }

    public int getPayable() {
        return payable;
    }

    public void setPayable(int payable) {
        this.payable = payable;
    }

    public Invoice withPayable(int payable) {
        this.payable = payable;
        return this;
    }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public Invoice withTax(int tax) {
        this.tax = tax;
        return this;
    }

    public int getNet() {
        return net;
    }

    public void setNet(int net) {
        this.net = net;
    }

    public Invoice withNet(int net) {
        this.net = net;
        return this;
    }

    public int getTotalPay() {
        return totalPay;
    }

    public void setTotalPay(int totalPay) {
        this.totalPay = totalPay;
    }

    public Invoice withTotalPay(int totalPay) {
        this.totalPay = totalPay;
        return this;
    }

    public int getTenderPay() {
        return tenderPay;
    }

    public void setTenderPay(int tenderPay) {
        this.tenderPay = tenderPay;
    }

    public Invoice withTenderPay(int tenderPay) {
        this.tenderPay = tenderPay;
        return this;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public Invoice withPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Invoice withStatus(String status) {
        this.status = status;
        return this;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Invoice withCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

}
