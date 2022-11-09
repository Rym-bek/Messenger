package com.example.telegram.models;

public class ChatMessage {
    String userMessage,userUID;
    long messageTime;


    public ChatMessage() { }

    public ChatMessage(String userMessage, long messageTime, String userUID) {
        this.userMessage = userMessage;
        this.messageTime = messageTime;
        this.userUID = userUID;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }
}
