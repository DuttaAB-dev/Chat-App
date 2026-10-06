package com.anisala.chat.tcp.dto;

public class LogInResponse implements DtoMarker {
    private UserObj user;
    private String message;

    public LogInResponse(UserObj user, String message) {
        this.user = user;
        this.message = message;
    }

    public UserObj getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return "LogInResponse{" +
                "user=" + user +
                ", message='" + message + '\'' +
                '}';
    }
}