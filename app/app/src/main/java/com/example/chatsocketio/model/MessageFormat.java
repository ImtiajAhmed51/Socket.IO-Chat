package com.example.chatsocketio.model;
public class MessageFormat {

    private String username;
    private String message;
    private String uniqueId;
    private String image;
    private String userPicture;
    private String time;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {

        this.image = image;
    }

    public MessageFormat(String uniqueId, String username, String message, String image,String userPicture,String time) {
        this.username = username;
        this.message = message;
        this.uniqueId = uniqueId;
        this.image=image;
        this.userPicture=userPicture;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserPicture() {
        return userPicture;
    }

    public void setUserPicture(String userPicture) {
        this.userPicture = userPicture;
    }

    public MessageFormat(String uniqueId, String username, String message) {
        this.username = username;
        this.message = message;
        this.uniqueId = uniqueId;
    }
    public MessageFormat(String uniqueId, String username, String message, String image) {
        this.username = username;
        this.message = message;
        this.uniqueId = uniqueId;
        this.image=image;
    }

    public MessageFormat(String uniqueId, String username, String message, String image,String userPicture) {
        this.username = username;
        this.message = message;
        this.uniqueId = uniqueId;
        this.image=image;
        this.userPicture=userPicture;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
