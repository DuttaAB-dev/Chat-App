package com.anisala.chat.server;

import com.anisala.chat.grpc.ChatServiceGrpc;
import com.anisala.chat.grpc.MessageRequest;
import com.anisala.chat.grpc.MessageResponse;

import io.grpc.stub.StreamObserver;

public class ChatServiceImpl
        extends ChatServiceGrpc.ChatServiceImplBase {

    @Override
    public void sendMessage(
            MessageRequest request,
            StreamObserver<MessageResponse> responseObserver) {

        System.out.println(
                "Message received:"
        );

        System.out.println(
                "From: " + request.getSender()
        );

        System.out.println(
                "To: " + request.getReceiver()
        );

        System.out.println(
                "Message: " + request.getMessage()
        );

        MessageResponse response =
                MessageResponse.newBuilder()
                        .setSuccess(true)
                        .setMessage("Message received by server")
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}