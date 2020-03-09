package com.chatho.chatho.pojo;

import com.google.gson.annotations.SerializedName;

public class send {

    @SerializedName("to")
    private String to;
    @SerializedName("data")
    private data data;


    public send(String to, com.chatho.chatho.pojo.data data) {
        this.to = to;
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public com.chatho.chatho.pojo.data getData() {
        return data;
    }

    public void setData(com.chatho.chatho.pojo.data data) {
        this.data = data;
    }
}
