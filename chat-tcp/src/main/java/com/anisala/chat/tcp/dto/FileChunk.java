package com.anisala.chat.tcp.dto;

public class FileChunk {
    private String transferId;
    private String fileName;
    private long fileSize;
    private int totalChunks;
    private int chunkNumber;
    private long chunkSize;
    private boolean lastChunk;
    private byte[] data;
    
    public FileChunk(String transferId, String fileName, long fileSize, int totalChunks, int chunkNumber, long chunkSize, boolean lastChunk, byte[] data) {
        this.transferId = transferId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.totalChunks = totalChunks;
        this.chunkNumber = chunkNumber;
        this.chunkSize = chunkSize;
        this.lastChunk = lastChunk;
        this.data = data;
    }
    
    public String getTransferId() {
        return transferId;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    public int getTotalChunks() {
        return totalChunks;
    }
    
    public int getChunkNumber() {
        return chunkNumber;
    }
    
    public long getChunkSize() {
        return chunkSize;
    }
    
    public boolean isLastChunk() {
        return lastChunk;
    }
    
    public byte[] getData() {
        return data;
    }

    public String toString() {
        return "FileChunk{" +
                "transferId='" + transferId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", totalChunks=" + totalChunks +
                ", chunkNumber=" + chunkNumber +
                ", chunkSize=" + chunkSize +
                ", lastChunk=" + lastChunk +
                '}';
    }
}