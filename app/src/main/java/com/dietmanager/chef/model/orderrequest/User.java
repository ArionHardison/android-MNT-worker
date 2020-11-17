package com.dietmanager.chef.model.orderrequest;

import com.google.gson.annotations.SerializedName;

public class User{

	@SerializedName("stripe_cust_id")
	private Object stripeCustId;

	@SerializedName("customer_support")
	private Object customerSupport;

	@SerializedName("device_id")
	private Object deviceId;

	@SerializedName("latitude")
	private Object latitude;

	@SerializedName("wallet_balance")
	private Integer walletBalance;

	@SerializedName("device_type")
	private String deviceType;

	@SerializedName("otp")
	private String otp;

	@SerializedName("avatar")
	private Object avatar;

	@SerializedName("is_verified")
	private Integer isVerified;

	@SerializedName("cuisines")
	private Object cuisines;

	@SerializedName("phone")
	private String phone;

	@SerializedName("social_unique_id")
	private Object socialUniqueId;

	@SerializedName("device_token")
	private Object deviceToken;

	@SerializedName("referral_code")
	private Object referralCode;

	@SerializedName("name")
	private String name;

	@SerializedName("referral_amount")
	private String referralAmount;

	@SerializedName("map_address")
	private Object mapAddress;

	@SerializedName("id")
	private Integer id;

	@SerializedName("login_by")
	private String loginBy;

	@SerializedName("email")
	private String email;

	@SerializedName("braintree_id")
	private Object braintreeId;

	@SerializedName("longitude")
	private Object longitude;

	public void setStripeCustId(Object stripeCustId){
		this.stripeCustId = stripeCustId;
	}

	public Object getStripeCustId(){
		return stripeCustId;
	}

	public void setCustomerSupport(Object customerSupport){
		this.customerSupport = customerSupport;
	}

	public Object getCustomerSupport(){
		return customerSupport;
	}

	public void setDeviceId(Object deviceId){
		this.deviceId = deviceId;
	}

	public Object getDeviceId(){
		return deviceId;
	}

	public void setLatitude(Object latitude){
		this.latitude = latitude;
	}

	public Object getLatitude(){
		return latitude;
	}

	public void setWalletBalance(Integer walletBalance){
		this.walletBalance = walletBalance;
	}

	public Integer getWalletBalance(){
		return walletBalance;
	}

	public void setDeviceType(String deviceType){
		this.deviceType = deviceType;
	}

	public String getDeviceType(){
		return deviceType;
	}

	public void setOtp(String otp){
		this.otp = otp;
	}

	public String getOtp(){
		return otp;
	}

	public void setAvatar(Object avatar){
		this.avatar = avatar;
	}

	public Object getAvatar(){
		return avatar;
	}

	public void setIsVerified(Integer isVerified){
		this.isVerified = isVerified;
	}

	public Integer getIsVerified(){
		return isVerified;
	}

	public void setCuisines(Object cuisines){
		this.cuisines = cuisines;
	}

	public Object getCuisines(){
		return cuisines;
	}

	public void setPhone(String phone){
		this.phone = phone;
	}

	public String getPhone(){
		return phone;
	}

	public void setSocialUniqueId(Object socialUniqueId){
		this.socialUniqueId = socialUniqueId;
	}

	public Object getSocialUniqueId(){
		return socialUniqueId;
	}

	public void setDeviceToken(Object deviceToken){
		this.deviceToken = deviceToken;
	}

	public Object getDeviceToken(){
		return deviceToken;
	}

	public void setReferralCode(Object referralCode){
		this.referralCode = referralCode;
	}

	public Object getReferralCode(){
		return referralCode;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setReferralAmount(String referralAmount){
		this.referralAmount = referralAmount;
	}

	public String getReferralAmount(){
		return referralAmount;
	}

	public void setMapAddress(Object mapAddress){
		this.mapAddress = mapAddress;
	}

	public Object getMapAddress(){
		return mapAddress;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public Integer getId(){
		return id;
	}

	public void setLoginBy(String loginBy){
		this.loginBy = loginBy;
	}

	public String getLoginBy(){
		return loginBy;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return email;
	}

	public void setBraintreeId(Object braintreeId){
		this.braintreeId = braintreeId;
	}

	public Object getBraintreeId(){
		return braintreeId;
	}

	public void setLongitude(Object longitude){
		this.longitude = longitude;
	}

	public Object getLongitude(){
		return longitude;
	}
}