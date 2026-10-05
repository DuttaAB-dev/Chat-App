package com.anisala.chat.client.service;

import com.anisala.chat.client.model.User;

public interface UserService {
    User getUser(String userName);
    boolean createUser(User user);
    User logIn(String userName);
    void logOut(String userId);
    String getCurrentUserName();
}
