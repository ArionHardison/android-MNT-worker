package com.dietmanager.chef.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Otp implements Serializable {

    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Otp")
    @Expose
    private Integer otp;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getOtp() {
        return otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }

}
