package com.chatho.chatho.Model;

import com.google.gson.annotations.SerializedName;

public class send {
    @SerializedName("to")
    private String to;
    @SerializedName("data")
    private data data;

    public send(String to, com.chatho.chatho.Model.data data) {
        this.to = to;
        this.data = data;
    }


    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public com.chatho.chatho.Model.data getData() {
        return data;
    }

    public void setData(com.chatho.chatho.Model.data data) {
        this.data = data;
    }
}
