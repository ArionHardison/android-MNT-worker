package com.dietmanager.chef.model.orderrequest;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class OrderRequestResponse implements Serializable {

	@SerializedName("orderRequest")
	private List<OrderRequestItem> orderRequest;

	public void setOrderRequest(List<OrderRequestItem> orderRequest){
		this.orderRequest = orderRequest;
	}

	public List<OrderRequestItem> getOrderRequest(){
		return orderRequest;
	}
}