package com.anisala.chat.tcp.dto;

import java.time.LocalDateTime;

public class ChatMessage implements DtoMarker {
    private String sender;
    private String receiver;
    private String message;
    private LocalDateTime timestamp;

    public ChatMessage(String sender, String receiver, String message, LocalDateTime timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timestamp = timestamp;
    }
    
    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }
    
    public String getMessage() {
        return message ;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String toString() {
        return "ChatMessage{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

