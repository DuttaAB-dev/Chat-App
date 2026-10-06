package com.anisala.chat.client.tcp;

import com.anisala.chat.client.service.ChatService;
import com.anisala.chat.tcp.*;
import com.anisala.chat.tcp.dto.ChatMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.time.Instant;
import java.util.function.BiConsumer;

public class ChatTcpService implements ChatService {
    public static volatile Message lastNonChatMessage = null;

    private final DataOutputStream out;
    private final DataInputStream in;
    private String currentUsername;
    private volatile boolean lastMessageFailed = false;
    private volatile boolean isListening = false;

    public ChatTcpService(DataOutputStream out, DataInputStream in) {
        this.out = out;
        this.in = in;
    }

    @Override
    public void startMessageListener(String userId, BiConsumer<String, String> listener) {
        this.currentUsername = userId;
        this.isListening = true;

        new Thread(() -> {
            try {
                while (isListening) {
                    Message msg = MessageHandler.readMessage(in);

                    if (msg.getMessageType() == MessageType.MESSAGE) {
                        ChatMessage chat = Deserialiser.deserialize(msg.getPayload(), ChatMessage.class);
                        listener.accept(chat.getSenderId(), chat.getMessage());
                    }
                    else if (msg.getMessageType() == MessageType.ERROR) {
                        lastMessageFailed = true;
                        String error = new String(msg.getPayload());
                        listener.accept("System", "[ERROR] " + error);
                    }
                    else {
                        lastNonChatMessage = msg;
                    }
                }
            } catch (Exception e) {
                if (isListening) {
                    listener.accept("System", "[ERROR] Connection lost.");
                }
            }
        }, "tcp-listener-" + userId).start();
    }

    @Override
    public void stopMessageListener() {
        this.isListening = false;
        this.currentUsername = null;
    }

    @Override
    public void sendMessage(String recipientId, String message) {
        if (currentUsername == null) return;
        lastMessageFailed = false; // Reset before sending
        
        try {
            ChatMessage chatMsg = new ChatMessage(currentUsername, recipientId, message, Instant.now());
            byte[] payload = Serialiser.serialize(chatMsg);
            MessageHandler.writeMessage(out, MessageType.MESSAGE, payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isOnline(String targetUser) {
        sendMessage(targetUser, "[PING]");
        
        try { Thread.sleep(100); } catch (InterruptedException e) {}
        
        return !lastMessageFailed;
    }
}
