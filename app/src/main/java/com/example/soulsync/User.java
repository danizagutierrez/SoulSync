package com.example.soulsync;

public class User {

    public String userFName;
    public String userLName;
    public String email;
    public String photoID;

    public User(String userFName, String userLName, String email, String photoID) {
        this.userFName = userFName;
        this.userLName = userLName;
        this.email = email;
        this.photoID = photoID;
    }

    public String getUserFName() {
        return userFName;
    }

    public void setUserFName(String userFName) {
        this.userFName = userFName;
    }

    public String getUserLName() {
        return userLName;
    }

    public void setUserLName(String userLName) {
        this.userLName = userLName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoID() {
        return photoID;
    }

    public void setPhotoID(String photoID) {
        this.photoID = photoID;
    }
}
