package com.anisala.chat.tcp.dto;

public class UserObj implements DtoMarker {
    private String userId;
    private String userName;
    private String name;
    // private String password;
    
    public UserObj(String userId, String userName, String name) {
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

    public String toString() {
        return "UserObj{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}