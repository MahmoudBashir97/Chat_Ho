package com.chatho.chatho.pojo;

public class Contacts {

    public String name, status, image, device_Tokens;

    public Contacts(String name, String status, String image, String device_Tokens) {
        this.name = name;
        this.status = status;
        this.image = image;
        this.device_Tokens = device_Tokens;
    }

    public Contacts() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDevice_Tokens() {
        return device_Tokens;
    }

    public void setDevice_Tokens(String device_Tokens) {
        this.device_Tokens = device_Tokens;
    }

}
