package com.chatho.chatho.pojo;

public class Group_Messages {

    private String From,Message,Type,messageID,time,date,name;

    public Group_Messages(String from, String message, String type, String messageID, String time, String date, String name) {
        From = from;
        Message = message;
        Type = type;
        this.messageID = messageID;
        this.time = time;
        this.date = date;
        this.name = name;
    }

    public Group_Messages() {
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        From = from;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
