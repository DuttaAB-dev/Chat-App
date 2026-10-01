package com.anisala.chat.tcp.dto;
public class LogOutResponse {
    // private boolean success;
    private String message;

    public LogOutResponse(String message) {
        // this.success = success;
        this.message = message;
    }

//     public boolean isSuccess() {
//         return success;
//     }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return "LogOutResponse{" +
                "message='" + message + '\'' +
                '}';
    }
}
