package com.anisala.chat.server.service;

import com.anisala.chat.server.model.Message;

public interface ClientEndpoint {
    void send(Message message);
    
    void disconnect();
}