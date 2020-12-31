package com.dietmanager.chef.model;

import com.google.gson.annotations.SerializedName;

public class PushCustomObject {

	@SerializedName("page")
	private String page;

	@SerializedName("order_id")
	private int orderId=0;

	public void setPage(String page){
		this.page = page;
	}

	public String getPage(){
		return page;
	}

	public void setOrderId(int orderId){
		this.orderId = orderId;
	}

	public int getOrderId(){
		return orderId;
	}
}