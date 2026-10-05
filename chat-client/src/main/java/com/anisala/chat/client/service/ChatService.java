package com.anisala.chat.client.service;

public interface ChatService {
    // void connect();
    void sendMessage(String recipient, String message);
    void startMessageListener(String username, java.util.function.BiConsumer<String, String> listener);
    void stopMessageListener();
    boolean isOnline(String targetUser);
}
