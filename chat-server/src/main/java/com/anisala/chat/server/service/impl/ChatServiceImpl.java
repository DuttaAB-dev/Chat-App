package com.anisala.chat.server.service.impl;

import com.anisala.chat.server.model.Message;
import com.anisala.chat.server.model.Session;
import com.anisala.chat.server.service.ChatService;
import com.anisala.chat.server.service.ClientEndpoint;
import com.anisala.chat.server.service.SessionManager;
import com.anisala.chat.server.service.UserService;
import com.anisala.chat.server.exception.UserOfflineException;

import java.time.Instant;

public class ChatServiceImpl implements ChatService {

    private final SessionManager sessionManager;
    private final UserService userService;

    public ChatServiceImpl(SessionManager sessionManager, UserService userService) {
        this.sessionManager = sessionManager;
        this.userService = userService;
    }

    @Override
    public void registerUser(String userId, ClientEndpoint endpoint) {
        Session newSession = new Session(userId, Instant.now(), Instant.now());
        sessionManager.createSession(newSession, endpoint);
        userService.setOnline(userId, true);
        System.out.println("User registered: " + userId);
    }

    @Override
    public void removeUser(String userId) {
        sessionManager.removeSession(userId);
        userService.setOnline(userId, false);
        userService.setLastSeen(userId, Instant.now());
        System.out.println("User disconnected: " + userId);
    }

    @Override
    public void sendMessage(Message message) {
        String receiverName = message.getReceiverUname();

        Session receiverSession = sessionManager.getSession(receiverName);
        if (receiverSession == null) {
            System.out.println("Message failed: User " + receiverName + " is offline.");
            throw new UserOfflineException("User " + receiverName + " is offline.");
        }

        else {
            ClientEndpoint receiverEndpoint = sessionManager.getEndpoint(receiverName);
            receiverEndpoint.send(message);
            receiverSession.updateActivity();
        }
    }
}