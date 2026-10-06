package com.anisala.chat.tcp.dto;

// unused
public class SendMessageRequest implements DtoMarker {
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
