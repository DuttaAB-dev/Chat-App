package com.anisala.chat.tcp.dto;

public class DownloadRequest implements DtoMarker {
    private String transferId;
    private String filename;
    
    public DownloadRequest(String transferId, String filename) {
        this.transferId = transferId;
        this.filename = filename;
    }
    
    public String getTransferId() {
        return transferId;
    }
    
    public String getFilename() {
        return filename;
    }

    public String toString() {
        return "DownloadRequest{" +
                "transferId='" + transferId + '\'' +
                ", filename='" + filename + '\'' +
                '}';
    }
}