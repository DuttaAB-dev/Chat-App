package com.anisala.chat.server.service;

import com.anisala.chat.server.model.Message;

public interface Connection {
    // Sends a pure domain Message to the client
    void send(Message message);
    
    // Closes the connection gracefully
    void disconnect();
}