package com.dietmanager.deliveryboy.model;

import com.google.gson.annotations.SerializedName;

public class Data {

	@SerializedName("unique_id")
	private int uniqueId;

	@SerializedName("updated_at")
	private String updatedAt;

	@SerializedName("name")
	private String name;

	@SerializedName("mobile")
	private String mobile;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("id")
	private int id;

	@SerializedName("email")
	private String email;

	public int getUniqueId(){
		return uniqueId;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	public String getName(){
		return name;
	}

	public String getMobile(){
		return mobile;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public int getId(){
		return id;
	}

	public String getEmail(){
		return email;
	}
}