package com.anisala.chat.server.gRPC;

import java.time.LocalDateTime;

import com.anisala.chat.proto.ChatMessage;
import com.anisala.chat.server.model.Message;
import com.anisala.chat.server.model.Session;
import com.anisala.chat.server.service.ChatService;
import com.anisala.chat.server.service.SessionManager;
import io.grpc.stub.StreamObserver;

public class ChatStreamObserver implements StreamObserver<ChatMessage> {

    private final StreamObserver<ChatMessage> responseObserver;
    private final ChatService chatService;
    private final SessionManager sessionManager;
    private final GrpcMessagePublisher messagePublisher;
    private String username;

    public ChatStreamObserver(
            StreamObserver<ChatMessage> responseObserver,
            ChatService chatService,
            SessionManager sessionManager,
            GrpcMessagePublisher messagePublisher) {
        this.responseObserver = responseObserver;
        this.chatService = chatService;
        this.sessionManager = sessionManager;
        this.messagePublisher = messagePublisher;
    }

    @Override
    public void onNext(ChatMessage protoMsg) {
        // Register session and stream on first message
        if (this.username == null && !protoMsg.getSender().isEmpty()) {
            this.username = protoMsg.getSender();
            sessionManager.addSession(new Session(this.username, LocalDateTime.now(), LocalDateTime.now(), null));
            messagePublisher.registerStream(this.username, this.responseObserver);
        }

        // Route chat message through domain service
        if (!protoMsg.getReceiver().isEmpty() && !protoMsg.getMessage().isEmpty()) {
            Message domainMsg = new Message(
                    protoMsg.getSender(),
                    protoMsg.getReceiver(),
                    protoMsg.getMessage()
            );
            chatService.routeMessage(domainMsg);
        }
    }

    @Override
    public void onError(Throwable t) {
        cleanup();
    }

    @Override
    public void onCompleted() {
        cleanup();
        responseObserver.onCompleted();
    }

    private void cleanup() {
        if (this.username != null) {
            sessionManager.removeSession(this.username);
            messagePublisher.unregisterStream(this.username);
        }
    }
}
