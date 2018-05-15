package com.dev.chatapp;

public class UserModel {
    private String id;
    private String name;
    private String email;
    private String password;
    private String profile_image;
    private String thumb_image;
    private String deviceToke;

    public UserModel() {
    }

    public UserModel(String id, String name) {
        this.id = id;
        this.name = name;
        this.profile_image = profile_image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getDeviceToke() {
        return deviceToke;
    }

    public void setDeviceToke(String deviceToke) {
        this.deviceToke = deviceToke;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
