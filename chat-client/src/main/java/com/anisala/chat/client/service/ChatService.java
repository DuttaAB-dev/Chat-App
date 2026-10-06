package com.anisala.chat.client.service;

public interface ChatService {
    // void connect();
    void sendMessage(String recipientId, String message);
    void startMessageListener(String userId, java.util.function.BiConsumer<String, String> listener);
    void stopMessageListener();
}
