package com.dietmanager.chef.model.orderrequest;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class OrderRequestResponse{

	@SerializedName("orderRequest")
	private List<OrderRequestItem> orderRequest;

	public void setOrderRequest(List<OrderRequestItem> orderRequest){
		this.orderRequest = orderRequest;
	}

	public List<OrderRequestItem> getOrderRequest(){
		return orderRequest;
	}
}