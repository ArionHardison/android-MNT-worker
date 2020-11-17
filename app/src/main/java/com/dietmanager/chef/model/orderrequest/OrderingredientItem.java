package com.dietmanager.chef.model.orderrequest;

import com.google.gson.annotations.SerializedName;

public class OrderingredientItem{

	@SerializedName("foodingredient")
	private Foodingredient foodingredient;

	@SerializedName("ingredient_id")
	private Integer ingredientId;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("id")
	private Integer id;

	@SerializedName("order_id")
	private Integer orderId;

	public void setFoodingredient(Foodingredient foodingredient){
		this.foodingredient = foodingredient;
	}

	public Foodingredient getFoodingredient(){
		return foodingredient;
	}

	public void setIngredientId(Integer ingredientId){
		this.ingredientId = ingredientId;
	}

	public Integer getIngredientId(){
		return ingredientId;
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

	public void setOrderId(Integer orderId){
		this.orderId = orderId;
	}

	public Integer getOrderId(){
		return orderId;
	}
}