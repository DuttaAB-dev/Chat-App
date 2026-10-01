package com.anisala.chat.tcp.dto;

public class UploadResponse {
    private boolean success;
    private String transferId;
    private String message;
    

    public UploadResponse(boolean success, String transferId, String message) {
        this.success = success;
        this.transferId = transferId;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getTransferId() {
        return transferId;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return "UploadResponse{" +
                "success=" + success +
                ", transferId='" + transferId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}