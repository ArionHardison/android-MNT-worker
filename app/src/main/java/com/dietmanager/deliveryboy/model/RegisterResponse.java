package com.dietmanager.deliveryboy.model;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {

	@SerializedName("Message")
	private String message;

	@SerializedName("Data")
	private Data data;

	public String getMessage(){
		return message;
	}

	public Data getData(){
		return data;
	}
}