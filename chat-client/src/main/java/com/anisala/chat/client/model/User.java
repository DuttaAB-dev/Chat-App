package com.anisala.chat.client.model;

public class User {
    private final String userId;
    private final String userName;
    private final String name;

    public User(String userName) {
        this.userName = userName;
        this.userId = null;
        this.name = null;
    }

    public User(String userName, String name) {
        this.userId = null;
        this.userName = userName;
        this.name = name;
    }

    public User(String userId, String userName, String name) {
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

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

}
