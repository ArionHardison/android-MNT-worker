package com.dietmanager.chef.model.orderrequest;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class OrderRequestResponse implements Serializable {

	@SerializedName("orders")
	private List<OrderRequestItem> orderRequest;

	@SerializedName("chef_status")
	private String chefStatus;

	public String getChefStatus() {
		return chefStatus;
	}

	public void setChefStatus(String chefStatus) {
		this.chefStatus = chefStatus;
	}

	public void setOrderRequest(List<OrderRequestItem> orderRequest){
		this.orderRequest = orderRequest;
	}

	public List<OrderRequestItem> getOrderRequest(){
		return orderRequest;
	}
}