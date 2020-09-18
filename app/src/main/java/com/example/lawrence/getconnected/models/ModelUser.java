package com.example.lawrence.getconnected.models;

public class ModelUser {
    String cover,email,image,phone,uid,username;
    public ModelUser() {
    }

    public ModelUser(String cover, String email, String image, String phone, String uid, String username) {
        this.cover = cover;
        this.email = email;
        this.image = image;
        this.phone = phone;
        this.uid = uid;
        this.username = username;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
