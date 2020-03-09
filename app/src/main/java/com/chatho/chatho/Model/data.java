package com.chatho.chatho.Model;

public class data {
    private String SenderId;
    private String SenderName;
    private String request;

    public data(String senderId, String senderName, String request) {
        SenderId = senderId;
        SenderName = senderName;
        this.request = request;
    }

    public String getSenderId() {
        return SenderId;
    }

    public void setSenderId(String senderId) {
        SenderId = senderId;
    }

    public String getSenderName() {
        return SenderName;
    }

    public void setSenderName(String senderName) {
        SenderName = senderName;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }
}
