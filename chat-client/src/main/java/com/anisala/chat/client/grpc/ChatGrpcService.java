package com.anisala.chat.client.grpc;

import com.anisala.chat.client.service.ChatService;
import com.anisala.chat.proto.ChatMessage;
import com.anisala.chat.proto.ChatServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import java.util.function.BiConsumer;

public class ChatGrpcService implements ChatService {

    private final ChatServiceGrpc.ChatServiceStub chatStub;
    private StreamObserver<ChatMessage> requestObserver;
    private String currentUsername;
    private boolean streamAlive = false;

    public ChatGrpcService(ManagedChannel channel) {
        this.chatStub = ChatServiceGrpc.newStub(channel);
    }

    @Override
    public void startMessageListener(String userId, BiConsumer<String, String> listener) {
        this.currentUsername = userId;
        this.streamAlive = true;

        this.requestObserver = chatStub.chatStream(new StreamObserver<ChatMessage>() {
            @Override
            public void onNext(ChatMessage msg) {
                if (msg.getSenderId().equals("System") && msg.getMessage().startsWith("[ERROR]")) {
                    listener.accept("System", msg.getMessage());
                } else {
                    listener.accept(msg.getSenderId(), msg.getMessage());
                }
            }

            @Override
            public void onError(Throwable t) {
                streamAlive = false;
                listener.accept("System", "[ERROR] Connection lost: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                streamAlive = false;
                listener.accept("System", "Disconnected from server.");
            }
        });

        sendMessage("System", "[INIT]");
    }

    @Override
    public void stopMessageListener() {
        if (requestObserver != null && streamAlive) {
            try {
                requestObserver.onCompleted(); 
            } catch (Exception e) {
                // ignore
            }
        }
        requestObserver = null;
        streamAlive = false;
        currentUsername = null;
    }

    @Override
    public void sendMessage(String recipientId, String message) {
        if (requestObserver != null && currentUsername != null && streamAlive) {
            requestObserver.onNext(ChatMessage.newBuilder()
                .setSenderId(currentUsername)
                .setReceiverId(recipientId)
                .setMessage(message)
                .build());
        }
    }

    public boolean isOnline(String targetUser) {
        if (!streamAlive) return false;
        
        sendMessage(targetUser, "[PING]");
        
        try { Thread.sleep(100); } catch (InterruptedException e) {}
        
        return streamAlive;
    }
}
