package com.anisala.chat.client;

import com.anisala.chat.proto.ChatServiceGrpc;
import com.anisala.chat.proto.SendMessageRequest;
import com.anisala.chat.proto.SendMessageResponse;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ChatClient {

    public static void main(String[] args) {

        ManagedChannel channel =
                ManagedChannelBuilder
                        .forAddress("localhost", 50051)
                        .usePlaintext()
                        .build();

        ChatServiceGrpc.ChatServiceBlockingStub stub =
                ChatServiceGrpc.newBlockingStub(channel);

        SendMessageRequest request =
                SendMessageRequest.newBuilder()
                        .setSender("Alice")
                        .setReceiver("Bob")
                        .setMessage("Hello Bob!")
                        .build();

        SendMessageResponse response =
                stub.sendMessage(request);

        System.out.println(
                "Server: " + response.getMessage()
        );

        channel.shutdown();
    }
}