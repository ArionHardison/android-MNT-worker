package com.dietmanager.chef.model.orderrequest;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CustomerAddress implements Serializable {

	@SerializedName("country")
	private String country;

	@SerializedName("pincode")
	private String pincode;

	@SerializedName("city")
	private String city;

	@SerializedName("latitude")
	private double latitude;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("type")
	private String type;

	@SerializedName("building")
	private String building;

	@SerializedName("user_id")
	private int userId;

	@SerializedName("street")
	private Object street;

	@SerializedName("map_address")
	private String mapAddress;

	@SerializedName("id")
	private int id;

	@SerializedName("state")
	private String state;

	@SerializedName("landmark")
	private String landmark;

	@SerializedName("longitude")
	private double longitude;

	public void setCountry(String country){
		this.country = country;
	}

	public String getCountry(){
		return country;
	}

	public void setPincode(String pincode){
		this.pincode = pincode;
	}

	public String getPincode(){
		return pincode;
	}

	public void setCity(String city){
		this.city = city;
	}

	public String getCity(){
		return city;
	}

	public void setLatitude(double latitude){
		this.latitude = latitude;
	}

	public double getLatitude(){
		return latitude;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setBuilding(String building){
		this.building = building;
	}

	public String getBuilding(){
		return building;
	}

	public void setUserId(int userId){
		this.userId = userId;
	}

	public int getUserId(){
		return userId;
	}

	public void setStreet(Object street){
		this.street = street;
	}

	public Object getStreet(){
		return street;
	}

	public void setMapAddress(String mapAddress){
		this.mapAddress = mapAddress;
	}

	public String getMapAddress(){
		return mapAddress;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setState(String state){
		this.state = state;
	}

	public String getState(){
		return state;
	}

	public void setLandmark(String landmark){
		this.landmark = landmark;
	}

	public String getLandmark(){
		return landmark;
	}

	public void setLongitude(double longitude){
		this.longitude = longitude;
	}

	public double getLongitude(){
		return longitude;
	}
}