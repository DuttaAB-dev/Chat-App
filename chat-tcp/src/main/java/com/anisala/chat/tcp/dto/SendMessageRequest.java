package com.anisala.chat.tcp.dto;

// unused
public class SendMessageRequest {
    private String message;

    public SendMessageRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return "SendMessageRequest{" +
                "message='" + message + '\'' +
                '}';
    }
}
