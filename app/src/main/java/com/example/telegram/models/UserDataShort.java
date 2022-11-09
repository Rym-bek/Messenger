package com.example.telegram.models;

public class UserDataShort {
    String uid, photo,nickname,name;

    public UserDataShort(){}

    public UserDataShort(String uid, String photo, String nickname, String name) {
        this.uid = uid;
        this.photo = photo;
        this.nickname = nickname;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
