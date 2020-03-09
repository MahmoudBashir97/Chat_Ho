package com.chatho.chatho.pojo;

public class notifymodel {

    private String name ;
    private String message;
    private String messsageRecieveID;
    private String messageSenderID;

    public notifymodel(String name, String message, String messsageRecieveID, String messageSenderID) {
        this.name = name;
        this.message = message;
        this.messsageRecieveID = messsageRecieveID;
        this.messageSenderID = messageSenderID;
    }

    public notifymodel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMesssageRecieveID() {
        return messsageRecieveID;
    }

    public void setMesssageRecieveID(String messsageRecieveID) {
        this.messsageRecieveID = messsageRecieveID;
    }

    public String getMessageSenderID() {
        return messageSenderID;
    }

    public void setMessageSenderID(String messageSenderID) {
        this.messageSenderID = messageSenderID;
    }


}
