package com.dietmanager.chef.model.orderrequest;

import java.util.List;

public class OngoingHistoryModel {

    private String headerName;
    private List<OrderRequestItem> orderList;

    public OngoingHistoryModel(String headerName, List<OrderRequestItem> orderList) {
        this.headerName = headerName;
        this.orderList = orderList;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public List<OrderRequestItem> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<OrderRequestItem> orderList) {
        this.orderList = orderList;
    }
}
