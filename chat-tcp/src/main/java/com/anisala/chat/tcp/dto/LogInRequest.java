package com.anisala.chat.tcp.dto;

public class LogInRequest {
    private String userName;

    public LogInRequest(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public String toString() {
        return "LogInRequest{" +
                "userName='" + userName + '\'' +
                '}';
    }
}
