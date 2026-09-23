package com.anisala.chat.server.service;

// import java.time.LocalDateTime;

import com.anisala.chat.server.model.Message;
import com.anisala.chat.server.model.Session;

public class ChatService {
    // private Message message;
    private SessionManager sessionManager;
    private MessagePublisher messagePublisher;

    public ChatService(SessionManager sessionManager, MessagePublisher messagePublisher) {
        this.sessionManager = sessionManager;
        this.messagePublisher = messagePublisher;
    }

    public void routeMessage(Message message){

        Session receiverSession = sessionManager.getSession(message.getReceiverUname());
        if (receiverSession != null) {
            receiverSession.deliver(message);
        }
        else{
            // Session sender = sessionManager.getSession(message.getSenderUname());
            Message offlineMessage = new Message(
                "Server",
                message.getSenderUname(),
                "User '" + message.getReceiverUname() + "' is currently offline."
            );
            messagePublisher.publish(offlineMessage);
            // offlineMessage.setReceiverUname(message.getSenderUname());
            // offlineMessage.setMessage(message.getMessage());
            // offlineMessage.setTimestamp(LocalDateTime.now().toString());
            // sender.getChatStream().onNext(offlineMessage);
            // sender.deliver(offlineMessage);
        }
    }
}