package com.dietmanager.chef.model.orderrequest;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Ingredient implements Serializable {

	@SerializedName("dietitian_id")
	private Object dietitianId;

	@SerializedName("code")
	private Object code;

	@SerializedName("price")
	private String price;

	@SerializedName("name")
	private String name;

	@SerializedName("unit_type_id")
	private Integer unitTypeId;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("id")
	private Integer id;

	@SerializedName("avatar")
	private String avatar;

	@SerializedName("quantity")
	private String quantity;

	public String getQuantity() {
		return quantity;
	}

	@SerializedName("unit_type")
	private UnitType unitType;

	@SerializedName("status")
	private String status;

	public void setDietitianId(Object dietitianId){
		this.dietitianId = dietitianId;
	}

	public Object getDietitianId(){
		return dietitianId;
	}

	public void setCode(Object code){
		this.code = code;
	}

	public Object getCode(){
		return code;
	}

	public void setPrice(String price){
		this.price = price;
	}

	public String getPrice(){
		return price;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setUnitTypeId(Integer unitTypeId){
		this.unitTypeId = unitTypeId;
	}

	public Integer getUnitTypeId(){
		return unitTypeId;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public Integer getId(){
		return id;
	}

	public void setAvatar(String avatar){
		this.avatar = avatar;
	}

	public String getAvatar(){
		return avatar;
	}

	public void setUnitType(UnitType unitType){
		this.unitType = unitType;
	}

	public UnitType getUnitType(){
		return unitType;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}
}