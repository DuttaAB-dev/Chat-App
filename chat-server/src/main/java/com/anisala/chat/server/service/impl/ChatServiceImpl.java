package com.anisala.chat.server.service.impl;

import com.anisala.chat.server.model.Message;
import com.anisala.chat.server.model.Session;
import com.anisala.chat.server.service.ChatService;
import com.anisala.chat.server.service.ClientEndpoint;
import com.anisala.chat.server.service.SessionManager;

import java.time.LocalDateTime;

public class ChatServiceImpl implements ChatService {

    private final SessionManager sessionManager;

    public ChatServiceImpl(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void registerUser(String userName, ClientEndpoint endpoint) {
        Session newSession = new Session(userName, LocalDateTime.now(), LocalDateTime.now());
        sessionManager.createSession(newSession, endpoint);

        System.out.println("User registered: " + userName);
    }

    @Override
    public void removeUser(String userName) {
        sessionManager.removeSession(userName);
        System.out.println("User disconnected: " + userName);
    }

    @Override
    public void sendMessage(Message message) {
        String receiverName = message.getReceiverUname();

        Session receiverSession = sessionManager.getSession(receiverName);
        if (receiverSession == null) {
            System.out.println("Message failed: User " + receiverName + " is offline.");
            return;
        }

        else {
            ClientEndpoint receiverEndpoint = sessionManager.getEndpoint(receiverName);
            receiverEndpoint.send(message);
            receiverSession.updateActivity();
        }
    }
}