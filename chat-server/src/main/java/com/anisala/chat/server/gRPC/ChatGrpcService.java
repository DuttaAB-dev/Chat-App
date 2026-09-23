package com.anisala.chat.server.gRPC;

import io.grpc.internal.Stream;
import io.grpc.stub.StreamObserver;
import com.anisala.chat.proto.ChatServiceGrpc;

import com.anisala.chat.server.service.ChatService;


public class ChatGrpcService extends ChatServiceGrpc.ChatServiceImplBase{
    private ChatService chatService;

    public ChatGrpcService(ChatService chatService) {
        this.chatService = chatService;
    }

    public StreamObserver<Message> chatStream(StreamObserver<Message>   responseObserver){
        return new StreamObserver<
        };
    }
}