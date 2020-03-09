package com.chatho.chatho.pojo;

public class Stories {
    private String Id;
    private String count;
    private String ImageURI;
    private boolean add;
    private String randomId;


    public Stories() {
    }

    public Stories(String Id,String count, String imageURI, boolean add ,String randomId) {
        this.Id=Id;
        this.count = count;
        ImageURI = imageURI;
        this.add = add;
        this.randomId=randomId;
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