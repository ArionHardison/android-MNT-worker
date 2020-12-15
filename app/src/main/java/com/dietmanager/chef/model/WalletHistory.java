package com.dietmanager.chef.model;

import com.google.gson.annotations.SerializedName;

public class WalletHistory {

	@SerializedName("transaction_id")
	private Object transactionId;

	@SerializedName("amount")
	private Double amount;

	@SerializedName("order_request_id")
	private int orderRequestId;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("message")
	private String message;

	@SerializedName("deleted_at")
	private Object deletedAt;

	@SerializedName("is_wallet")
	private int isWallet;

	@SerializedName("dietitian_id")
	private int dietitianId;

	@SerializedName("subscription_id")
	private Object subscriptionId;

	@SerializedName("chef_id")
	private Object chefId;

	@SerializedName("user_id")
	private Object userId;

	@SerializedName("id")
	private int id;

	@SerializedName("order_id")
	private Object orderId;

	@SerializedName("status")
	private String status;

	public Object getTransactionId(){
		return transactionId;
	}

	public Double getAmount(){
		return amount;
	}

	public int getOrderRequestId(){
		return orderRequestId;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public String getMessage(){
		return message;
	}

	public Object getDeletedAt(){
		return deletedAt;
	}

	public int getIsWallet(){
		return isWallet;
	}

	public int getDietitianId(){
		return dietitianId;
	}

	public Object getSubscriptionId(){
		return subscriptionId;
	}

	public Object getChefId(){
		return chefId;
	}

	public Object getUserId(){
		return userId;
	}

	public int getId(){
		return id;
	}

	public Object getOrderId(){
		return orderId;
	}

	public String getStatus(){
		return status;
	}
}