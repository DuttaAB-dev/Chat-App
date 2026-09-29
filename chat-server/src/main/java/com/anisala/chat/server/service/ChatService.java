package com.anisala.chat.server.service;

import com.anisala.chat.server.model.Message;

public interface ChatService {
    
    // Handles a new user logging in
    void registerUser(String userName, ClientEndpoint endpoint);
    
    // Handles a user logging out or disconnecting
    void removeUser(String userName);
    
    // Handles routing a message
    void sendMessage(Message message);
}
