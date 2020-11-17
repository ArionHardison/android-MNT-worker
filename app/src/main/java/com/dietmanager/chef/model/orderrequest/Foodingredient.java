package com.dietmanager.chef.model.orderrequest;

import com.google.gson.annotations.SerializedName;

public class Foodingredient{

	@SerializedName("quantity")
	private String quantity;

	@SerializedName("ingredient")
	private Ingredient ingredient;

	@SerializedName("ingredient_id")
	private Integer ingredientId;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("id")
	private Integer id;

	@SerializedName("food_id")
	private Integer foodId;

	@SerializedName("status")
	private String status;

	public void setQuantity(String quantity){
		this.quantity = quantity;
	}

	public String getQuantity(){
		return quantity;
	}

	public void setIngredient(Ingredient ingredient){
		this.ingredient = ingredient;
	}

	public Ingredient getIngredient(){
		return ingredient;
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

	public void setFoodId(Integer foodId){
		this.foodId = foodId;
	}

	public Integer getFoodId(){
		return foodId;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}
}