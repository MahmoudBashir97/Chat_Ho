package com.chatho.chatho.pojo;

public class data {

    private String senderId;
    private String senderName;
    private String receiverId;
    private String message;
    private String imageReceiver;
    private String countbadge;
    private String request;

    public data(String senderId, String senderName, String receiverId, String message, String imageReceiver, String countbadge,String request) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.message = message;
        this.imageReceiver = imageReceiver;
        this.countbadge = countbadge;
        this.request=request;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageReceiver() {
        return imageReceiver;
    }

    public void setImageReceiver(String imageReceiver) {
        this.imageReceiver = imageReceiver;
    }

    public String getCountbadge() {
        return countbadge;
    }

    public void setCountbadge(String countbadge) {
        this.countbadge = countbadge;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }
}