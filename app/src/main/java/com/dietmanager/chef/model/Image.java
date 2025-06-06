package com.dietmanager.chef.model;

/**
 * Created by santhosh@appoets.com on 05-10-2017.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class Image {
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("position")
    @Expose
    private Integer position;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Image withUrl(String url) {
        this.url = url;
        return this;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Image withPosition(Integer position) {
        this.position = position;
        return this;
    }
}
