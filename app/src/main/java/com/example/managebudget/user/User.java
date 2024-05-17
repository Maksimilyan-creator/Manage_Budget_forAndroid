package com.example.managebudget.user;

public class User
{
    private String username;
    private String userEmail;
    private String profileImage;

    public User(String username, String userEmail, String profileImage) {
        this.username = username;
        this.userEmail = userEmail;
        this.profileImage = profileImage;
    }

    public String getUsername() {
        return username;
    }
    public String getUserEmail() {return userEmail;}

    public String getProfileImage() {
        return profileImage;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserEmail(String userEmail) {this.userEmail = userEmail;}

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
