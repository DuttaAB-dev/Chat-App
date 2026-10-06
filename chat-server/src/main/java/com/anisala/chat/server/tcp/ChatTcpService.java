package com.anisala.chat.server.tcp;

import com.anisala.chat.server.model.Message;
import com.anisala.chat.server.service.ChatService;
import com.anisala.chat.server.service.ClientEndpoint;
import com.anisala.chat.tcp.dto.ChatMessage;

import java.time.ZoneOffset;
import java.time.Instant;

public class ChatTcpService {

    private final ChatService chatService;

    public ChatTcpService(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Feature 1: Activate Live Connection
     * Called when a client establishes their TCP socket. We pass their username
     * and a wrapped ClientEndpoint (which has the socket OutputStream) to the backend.
     * The ChatService/SessionManager saves this so it can route messages to them.
     */
    public void startChatSession(String userName, ClientEndpoint endpoint) {
        if (userName == null || endpoint == null) {
            throw new IllegalArgumentException("Cannot start session with null data");
        }
        chatService.registerUser(userName, endpoint);
    }

    /**
     * Feature 2: Clean up Dropped Connection
     * Called when a TCP socket gracefully ends, or crashes (EOFException).
     * Prevents the backend from trying to write messages to broken pipes.
     */
    public void terminateChatSession(String userName) {
        if (userName != null) {
            chatService.removeUser(userName);
        }
    }

    /**
     * Feature 3: Route an Incoming Chat
     * Transforms a raw network DTO (ChatMessage) into the pristine Backend Model (Message)
     * and hands it off to ChatService for delivery.
     */
    public void processIncomingMessage(ChatMessage networkMessage) {
        
        if (networkMessage.getSenderId() == null || networkMessage.getReceiverId() == null) {
            System.err.println("Dropped invalid message: Missing sender or receiver");
            return; 
        }

        Instant messageTimestamp = (networkMessage.getTimestamp() != null)
                ? networkMessage.getTimestamp()
                : Instant.now();

        Message domainMessage = new Message(
            networkMessage.getSenderId(),
            networkMessage.getReceiverId(),
            networkMessage.getMessage(),
            messageTimestamp
        );

        // 4. Send to the engine! 
        // Inside your ChatServiceImpl, it will look up the receiver's Session, 
        // grab their mapped ClientEndpoint, and call endpoint.send().
        chatService.sendMessage(domainMessage);
    }
}
