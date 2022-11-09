package com.example.telegram.models;

import com.google.firebase.storage.StorageReference;

public class UserDialogs {
    String uid, nickname, name, userMessage;
    StorageReference userPhoto;
    int keyPhoto;
    long messageTime;

    public UserDialogs() { }

    public UserDialogs(String uid, String nickname, String name, String userMessage, StorageReference userPhoto, int keyPhoto, long messageTime) {
        this.uid = uid;
        this.nickname = nickname;
        this.name = name;
        this.userMessage = userMessage;
        this.userPhoto = userPhoto;
        this.keyPhoto = keyPhoto;
        this.messageTime = messageTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public StorageReference getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(StorageReference userPhoto) {
        this.userPhoto = userPhoto;
    }

    public int getKeyPhoto() {
        return keyPhoto;
    }

    public void setKeyPhoto(int keyPhoto) {
        this.keyPhoto = keyPhoto;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
