package com.anisala.chat.server.service.impl;

import com.anisala.chat.server.model.Message;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.anisala.chat.server.service.ChatService;
import com.anisala.chat.server.service.Connection;

public class ChatServiceImpl2 implements ChatService {
    // One simple map: Username -> Network Socket
    private final Map<String, Connection> activeUsers = new ConcurrentHashMap<>();

    @Override 
    public void registerUser(String userName, String ip, Connection connection) {
        activeUsers.put(userName, connection);
        System.out.println(userName + " joined the chat.");
    }

    public void removeUser(String userName) {
        activeUsers.remove(userName);
        System.out.println(userName + " left the chat.");
    }

    public void sendMessage(Message message) {
        Connection receiverConnection = activeUsers.get(message.getReceiverUname());
        if (receiverConnection != null) {
            receiverConnection.send(message);
        } else {
            System.out.println("Cannot send: " + message.getReceiverUname() + " is offline.");
        }
    }
}