package com.anisala.chat.tcp.dto;

public class CreateUserResponse implements DtoMarker {
    private boolean success;
    private String message;
    
    public CreateUserResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getMessage() {
        return message;
    }

    public String toString() {
        return "CreateUserResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}