package com.anisala.chat.tcp;

public class Message {
    private byte messageType;
    private byte[] payload;

    public Message(byte messageType, byte[] payload) {
        this.messageType = messageType;
        this.payload = payload;
    }

    public byte getMessageType() {
        return messageType;
    }

    public byte[] getPayload() {
        return payload;
    }
    
}
