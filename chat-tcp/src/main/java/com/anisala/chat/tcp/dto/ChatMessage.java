package com.anisala.chat.tcp.dto;

import java.time.Instant;

public class ChatMessage implements DtoMarker {
    private String senderId;
    private String receiverId;
    private String message;
    private Instant timestamp;

    public ChatMessage(String senderId, String receiverId, String message, Instant timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = timestamp;
    }
    
    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }
    
    public String getMessage() {
        return message ;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }

    public String toString() {
        return "ChatMessage{" +
                "senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

