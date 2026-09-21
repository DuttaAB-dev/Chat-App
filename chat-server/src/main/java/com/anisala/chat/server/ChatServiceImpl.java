package com.anisala.chat.server;

import com.anisala.chat.proto.ChatServiceGrpc;
import com.anisala.chat.proto.SendMessageRequest;
import com.anisala.chat.proto.SendMessageResponse;

import io.grpc.stub.StreamObserver;

public class ChatServiceImpl
        extends ChatServiceGrpc.ChatServiceImplBase {

    @Override
    public void sendMessage(
            SendMessageRequest request,
            StreamObserver<SendMessageResponse> responseObserver) {

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

        SendMessageResponse response =
                SendMessageResponse.newBuilder()
                        .setSuccess(true)
                        .setMessage("Message received by server")
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}