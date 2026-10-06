package com.anisala.chat.tcp.dto;

//unused
public class SendMessageResponse implements DtoMarker {
    private String message;

    public SendMessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return "SendMessageResponse{" +
                "message='" + message + '\'' +
                '}';
    }
}