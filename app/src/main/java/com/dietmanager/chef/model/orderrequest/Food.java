package com.dietmanager.chef.model.orderrequest;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Food implements Serializable {

	@SerializedName("code")
	private Object code;

	@SerializedName("time_category_id")
	private String timeCategoryId;

	@SerializedName("description")
	private String description;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("avatar")
	private Object avatar;

	@SerializedName("time_category")
	private TimeCategory timeCategory;

	@SerializedName("dietitian_id")
	private Object dietitianId;

	@SerializedName("price")
	private String price;

	@SerializedName("name")
	private String name;

	@SerializedName("days")
	private Integer days;

	@SerializedName("id")
	private Integer id;

	@SerializedName("status")
	private String status;

	@SerializedName("who")
	private String who;

	public void setCode(Object code){
		this.code = code;
	}

	public Object getCode(){
		return code;
	}

	public void setTimeCategoryId(String timeCategoryId){
		this.timeCategoryId = timeCategoryId;
	}

	public String getTimeCategoryId(){
		return timeCategoryId;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setAvatar(Object avatar){
		this.avatar = avatar;
	}

	public Object getAvatar(){
		return avatar;
	}

	public void setTimeCategory(TimeCategory timeCategory){
		this.timeCategory = timeCategory;
	}

	public TimeCategory getTimeCategory(){
		return timeCategory;
	}

	public void setDietitianId(Object dietitianId){
		this.dietitianId = dietitianId;
	}

	public Object getDietitianId(){
		return dietitianId;
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

	public void setDays(Integer days){
		this.days = days;
	}

	public Integer getDays(){
		return days;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public Integer getId(){
		return id;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	public void setWho(String who){
		this.who = who;
	}

	public String getWho(){
		return who;
	}
}