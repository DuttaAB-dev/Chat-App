package com.anisala.chat.tcp.dto;

public class LogOutRequest {
    private String userId;

    public LogOutRequest(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String toString() {
        return "LogOutRequest{" +
                "userId='" + userId + '\'' +
                '}';
    }
}