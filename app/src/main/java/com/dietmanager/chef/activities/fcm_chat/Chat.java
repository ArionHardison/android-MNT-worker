package com.dietmanager.chef.activities.fcm_chat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by santhosh@appoets.com on 23-01-2018.
 */

public class Chat {
    @SerializedName("sender")
    @Expose
    private String sender;
    @SerializedName("image")
    @Expose
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @SerializedName("timestamp")
    @Expose
    private Long timestamp;
    @SerializedName("name")
    @Expose
    private String name="";
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("read")
    @Expose
    private Integer read;
    @SerializedName("readedMembers")
    @Expose
    private ArrayList<Integer> readedMembers;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getRead() {
        return read;
    }

    public void setRead(Integer read) {
        this.read = read;
    }

    public ArrayList<Integer> getReadedMembers() {
        return readedMembers;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setReadedMembers(ArrayList<Integer> readedMembers) {
        this.readedMembers = readedMembers;
    }
}
