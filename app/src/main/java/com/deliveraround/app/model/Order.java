package com.deliveraround.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 05-10-2017.
 */

public class Order {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("invoice_id")
    @Expose
    private String invoiceId;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("user_address_id")
    @Expose
    private Integer userAddressId;
    @SerializedName("shop_id")
    @Expose
    private Integer shopId;
    @SerializedName("transporter_id")
    @Expose
    private Integer transporterId;
    @SerializedName("transporter_vehicle_id")
    @Expose
    private Integer transporterVehicleId;
    @SerializedName("route_key")
    @Expose
    private String routeKey;
    @SerializedName("dispute")
    @Expose
    private Object dispute;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("response_time")
    @Expose
    private Integer responseTime;
    @SerializedName("dispute_manager")
    @Expose
    private List<DisputeManager> disputeManager = null;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("transporter")
    @Expose
    private Transporter transporter;
    @SerializedName("vehicles")
    @Expose
    private Vehicles vehicles;
    @SerializedName("invoice")
    @Expose
    private Invoice invoice;
    @SerializedName("address")
    @Expose
    private Address address;
    @SerializedName("shop")
    @Expose
    private Shop shop;
    @SerializedName("items")
    @Expose
    private List<Item> items = null;
    @SerializedName("ordertiming")
    @Expose
    private List<Ordertiming> ordertiming = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Order withId(Integer id) {
        this.id = id;
        return this;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Order withInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
        return this;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Order withUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public Integer getUserAddressId() {
        return userAddressId;
    }

    public void setUserAddressId(Integer userAddressId) {
        this.userAddressId = userAddressId;
    }

    public Order withUserAddressId(Integer userAddressId) {
        this.userAddressId = userAddressId;
        return this;
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public Order withShopId(Integer shopId) {
        this.shopId = shopId;
        return this;
    }

    public Integer getTransporterId() {
        return transporterId;
    }

    public void setTransporterId(Integer transporterId) {
        this.transporterId = transporterId;
    }

    public Order withTransporterId(Integer transporterId) {
        this.transporterId = transporterId;
        return this;
    }

    public Integer getTransporterVehicleId() {
        return transporterVehicleId;
    }

    public void setTransporterVehicleId(Integer transporterVehicleId) {
        this.transporterVehicleId = transporterVehicleId;
    }

    public Order withTransporterVehicleId(Integer transporterVehicleId) {
        this.transporterVehicleId = transporterVehicleId;
        return this;
    }

    public String getRouteKey() {
        return routeKey;
    }

    public void setRouteKey(String routeKey) {
        this.routeKey = routeKey;
    }

    public Order withRouteKey(String routeKey) {
        this.routeKey = routeKey;
        return this;
    }

    public Object getDispute() {
        return dispute;
    }

    public void setDispute(Object dispute) {
        this.dispute = dispute;
    }

    public Order withDispute(Object dispute) {
        this.dispute = dispute;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Order withStatus(String status) {
        this.status = status;
        return this;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Order withCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Order withUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Integer getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Integer responseTime) {
        this.responseTime = responseTime;
    }

    public Order withResponseTime(Integer responseTime) {
        this.responseTime = responseTime;
        return this;
    }

    public List<DisputeManager> getDisputeManager() {
        return disputeManager;
    }

    public void setDisputeManager(List<DisputeManager> disputeManager) {
        this.disputeManager = disputeManager;
    }

    public Order withDisputeManager(List<DisputeManager> disputeManager) {
        this.disputeManager = disputeManager;
        return this;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Order withUser(User user) {
        this.user = user;
        return this;
    }

    public Transporter getTransporter() {
        return transporter;
    }

    public void setTransporter(Transporter transporter) {
        this.transporter = transporter;
    }

    public Order withTransporter(Transporter transporter) {
        this.transporter = transporter;
        return this;
    }

    public Vehicles getVehicles() {
        return vehicles;
    }

    public void setVehicles(Vehicles vehicles) {
        this.vehicles = vehicles;
    }

    public Order withVehicles(Vehicles vehicles) {
        this.vehicles = vehicles;
        return this;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Order withInvoice(Invoice invoice) {
        this.invoice = invoice;
        return this;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Order withAddress(Address address) {
        this.address = address;
        return this;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Order withShop(Shop shop) {
        this.shop = shop;
        return this;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Order withItems(List<Item> items) {
        this.items = items;
        return this;
    }

    public List<Ordertiming> getOrdertiming() {
        return ordertiming;
    }

    public void setOrdertiming(List<Ordertiming> ordertiming) {
        this.ordertiming = ordertiming;
    }

    public Order withOrdertiming(List<Ordertiming> ordertiming) {
        this.ordertiming = ordertiming;
        return this;
    }
}
