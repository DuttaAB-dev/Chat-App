package com.anisala.chat.server.dto;

public class UserDto {
    private String userId;
    private String userName;
    private String name;
    
    public UserDto(String userId, String userName, String name) {
        this.userId = userId;
        this.userName = userName;
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getName() {
        return name;
    }
    
}
