package com.example.managebudget.users;

import com.google.firebase.database.PropertyName;

public class Users {
    String id;
    String username;

    String userEmail;
    String profileImage;

    public Users(String id, String username, String userEmail, String profileImage) {
        this.id = id;
        this.username = username;
        this.userEmail = userEmail;
        this.profileImage = profileImage;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
