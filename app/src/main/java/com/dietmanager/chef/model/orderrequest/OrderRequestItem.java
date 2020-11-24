package com.dietmanager.chef.model.orderrequest;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class OrderRequestItem implements Serializable {

	@SerializedName("schedule_at")
	private Object scheduleAt;

	@SerializedName("rating")
	private List<Object> rating;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("customer_address")
	private CustomerAddress customerAddress;

	public CustomerAddress getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(CustomerAddress customerAddress) {
		this.customerAddress = customerAddress;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	@SerializedName("payment_mode")
	private String paymentMode="";

	public String getPaymentMode() {
		return paymentMode;
	}

	@SerializedName("discount")
	private String discount;

	@SerializedName("ingredient_image")
	private String ingredientImage;

	@SerializedName("orderingredient")
	private List<OrderingredientItem> orderingredient;

	@SerializedName("dietitian")
	private Dietitian dietitian;

	@SerializedName("food_id")
	private Integer foodId;

	@SerializedName("food")
	private Food food;

	@SerializedName("dietitian_rating")
	private Integer dietitianRating;

	@SerializedName("dietitian_id")
	private Integer dietitianId;

	@SerializedName("chef_rating")
	private Integer chefRating;

	@SerializedName("total")
	private String total;

	@SerializedName("chef_id")
	private Integer chefId;

	@SerializedName("payable")
	private String payable;
	@SerializedName("tax")
	private String tax;

	public String getTax() {
		return tax;
	}

	public void setTax(String tax) {
		this.tax = tax;
	}

	@SerializedName("user_id")
	private Integer userId;

	@SerializedName("id")
	private Integer id;

	@SerializedName("is_scheduled")
	private Integer isScheduled;

	@SerializedName("user")
	private User user;

	@SerializedName("status")
	private String status;

	public void setScheduleAt(Object scheduleAt){
		this.scheduleAt = scheduleAt;
	}

	public Object getScheduleAt(){
		return scheduleAt;
	}

	public void setRating(List<Object> rating){
		this.rating = rating;
	}

	public List<Object> getRating(){
		return rating;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setDiscount(String discount){
		this.discount = discount;
	}

	public String getDiscount(){
		return discount;
	}

	public void setIngredientImage(String ingredientImage){
		this.ingredientImage = ingredientImage;
	}

	public String getIngredientImage(){
		return ingredientImage;
	}

	public void setOrderingredient(List<OrderingredientItem> orderingredient){
		this.orderingredient = orderingredient;
	}

	public List<OrderingredientItem> getOrderingredient(){
		return orderingredient;
	}

	public void setDietitian(Dietitian dietitian){
		this.dietitian = dietitian;
	}

	public Dietitian getDietitian(){
		return dietitian;
	}

	public void setFoodId(Integer foodId){
		this.foodId = foodId;
	}

	public Integer getFoodId(){
		return foodId;
	}

	public void setFood(Food food){
		this.food = food;
	}

	public Food getFood(){
		return food;
	}

	public void setDietitianRating(Integer dietitianRating){
		this.dietitianRating = dietitianRating;
	}

	public Integer getDietitianRating(){
		return dietitianRating;
	}

	public void setDietitianId(Integer dietitianId){
		this.dietitianId = dietitianId;
	}

	public Integer getDietitianId(){
		return dietitianId;
	}

	public void setChefRating(Integer chefRating){
		this.chefRating = chefRating;
	}

	public Integer getChefRating(){
		return chefRating;
	}

	public void setTotal(String total){
		this.total = total;
	}

	public String getTotal(){
		return total;
	}

	public void setChefId(Integer chefId){
		this.chefId = chefId;
	}

	public Integer getChefId(){
		return chefId;
	}

	public void setPayable(String payable){
		this.payable = payable;
	}

	public String getPayable(){
		return payable;
	}

	public void setUserId(Integer userId){
		this.userId = userId;
	}

	public Integer getUserId(){
		return userId;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public Integer getId(){
		return id;
	}

	public void setIsScheduled(Integer isScheduled){
		this.isScheduled = isScheduled;
	}

	public Integer getIsScheduled(){
		return isScheduled;
	}

	public void setUser(User user){
		this.user = user;
	}

	public User getUser(){
		return user;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}
}