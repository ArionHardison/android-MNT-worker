package com.dietmanager.chef.model.orderrequest;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UnitType implements Serializable {

	@SerializedName("name")
	private String name;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("id")
	private int id;

	@SerializedName("status")
	private String status;

	public String getName(){
		return name;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public int getId(){
		return id;
	}

	public String getStatus(){
		return status;
	}
}