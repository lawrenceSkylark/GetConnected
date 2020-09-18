package com.example.lawrence.getconnected.models;

import androidx.annotation.NonNull;

public class Message {
    String message;
    String username;
    String key;
    public Message(){}

    public Message(String message, String username) {
        this.message = message;
        this.username = username;
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @NonNull
    @Override
    public String toString() {
        return "Message{+" +
                "message='"+ message + '\''+
                ",username='"+username + '\''+
                ",key='"+key + '\''+
                '}';
    }
}
