package com.anisala.chat.server.service;

import java.time.LocalDateTime;

import com.anisala.chat.server.model.Message;
import com.anisala.chat.server.model.Session;

public class ChatService {
    // private Message message;
    private SessionManager sessionManager;

    public ChatService(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void sendMessage(Message message){

        Session receiver = sessionManager.getSession(message.getReceiverUname());
        if (receiver != null) {
            receiver.getChatStream().onNext(message);
        }
        else{
            Session sender = sessionManager.getSession(message.getSenderUname());
            Message offlineMessage = new Message();
            offlineMessage.setSenderUname("Server");
            offlineMessage.setReceiverUname(message.getSenderUname());
            offlineMessage.setMessage(message.getMessage());
            offlineMessage.setTimestamp(LocalDateTime.now().toString());
            sender.getChatStream().onNext(offlineMessage);
        }
    }
}