package com.anisala.chat.server.service.impl;

import com.anisala.chat.server.model.Message;
import com.anisala.chat.server.model.Session;
import com.anisala.chat.server.service.ChatService;
import com.anisala.chat.server.service.ClientEndpoint;
// import com.anisala.chat.server.service.ConnectionManager;
import com.anisala.chat.server.service.SessionManager;

import java.time.LocalDateTime;

public class ChatServiceImpl implements ChatService {

    private final SessionManager sessionManager;
    // private final ConnectionManager connectionManager;

    // Dependencies are injected via the constructor
    public ChatServiceImpl(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        // this.connectionManager = connectionManager;
    }

    @Override
    public void registerUser(String userName, ClientEndpoint endpoint) {
        // 1. Create and store the State (Model)
        Session newSession = new Session(userName, LocalDateTime.now(), LocalDateTime.now());
        sessionManager.createSession(newSession, endpoint);

        // 2. Store the Transport Network Pipe (Action)
        // connectionManager.addConnection(userName, connection);
        
        System.out.println("User registered: " + userName);
    }

    @Override
    public void removeUser(String userName) {
        // Clean up both State and Transport
        sessionManager.removeSession(userName);
        // connectionManager.removeConnection(userName);
        
        System.out.println("User disconnected: " + userName);
    }

    @Override
    public void sendMessage(Message message) {
        String receiverName = message.getReceiverUname();

        // 1. Check business state: Is the user logged in?
        Session receiverSession = sessionManager.getSession(receiverName);
        if (receiverSession == null) {
            // Future feature: Send to an OfflineMessageRepository database here
            System.out.println("Message failed: User " + receiverName + " is offline.");
            return;
        }

        // 2. Fetch the active network transport
        // Connection receiverConnection = connectionManager.getConnection(receiverName);

        // 3. Execute the action and update state
        // if (receiverConnection != null) {
        //     receiverConnection.send(message);
        //     
        //     // Update their last active timestamp
        //     receiverSession.updateActivity(); 
        // } 
        else {
            ClientEndpoint receiverEndpoint = sessionManager.getEndpoint(receiverName);
            receiverEndpoint.send(message);
            receiverSession.updateActivity();
            // Edge Case: Session exists in DB, but the live socket dropped unexpectedly
            // System.out.println("Error: " + receiverName + " has no active socket. Cleaning up.");
            // removeUser(receiverName);
        }
    }
}