package com.anisala.chat.tcp.dto;

public class GetUserRequest implements DtoMarker {
    private String userName;

    public GetUserRequest(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public String toString() {
        return "GetUserRequest{" +
                "userName='" + userName + '\'' +
                '}';
    }
}