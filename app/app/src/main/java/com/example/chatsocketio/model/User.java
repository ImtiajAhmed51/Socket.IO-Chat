package com.example.chatsocketio.model;


public class User{
    private String id;
    private String userName;
    private String userImage;
    private Boolean isOnline;
    public User() {
    }

    public User(String id, String userName, String userImage, Boolean isOnline) {
        this.id = id;
        this.userName = userName;
        this.userImage = userImage;
        this.isOnline = isOnline;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }
}
