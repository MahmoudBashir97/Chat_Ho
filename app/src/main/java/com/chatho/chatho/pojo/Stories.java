package com.chatho.chatho.pojo;

public class Stories {
    private String Id;
    private String count;
    private String ImageURI;
    private boolean add;
    private String randomId;
    private String time;
    private String date;



    public Stories() {
    }

    public Stories(String id, String count, String imageURI, boolean add, String randomId, String time, String date) {
        Id = id;
        this.count = count;
        ImageURI = imageURI;
        this.add = add;
        this.randomId = randomId;
        this.time = time;
        this.date = date;
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

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getImageURI() {
        return ImageURI;
    }

    public void setImageURI(String imageURI) {
        ImageURI = imageURI;
    }

    public boolean isAdd() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getRandomId() {
        return randomId;
    }

    public void setRandomId(String randomId) {
        this.randomId = randomId;
    }
}