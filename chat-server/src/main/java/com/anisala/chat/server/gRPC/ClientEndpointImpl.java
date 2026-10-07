package com.anisala.chat.server.gRPC;

import com.anisala.chat.proto.ChatMessage;
import com.anisala.chat.server.model.Message;
import com.anisala.chat.server.service.ClientEndpoint;
import com.anisala.chat.server.util.TimestampConverter;

import io.grpc.stub.StreamObserver;

public class ClientEndpointImpl implements ClientEndpoint {

    private final StreamObserver<ChatMessage> responseObserver;

    public ClientEndpointImpl(StreamObserver<ChatMessage> responseObserver) {
        this.responseObserver = responseObserver;
    }

    @Override
    public void send(Message message) {
        // Map pure Java model to gRPC Protobuf model
        ChatMessage protoMessage = ChatMessage.newBuilder()
                .setSenderId(message.getSenderId())
                .setReceiverId(message.getReceiverId())
                .setMessage(message.getMessage())
                .setTimeStamp(TimestampConverter.toProtoTimestamp(message.getTimestamp()))
                .build();

        // Push over the network
        responseObserver.onNext(protoMessage);
    }

    @Override
    public void disconnect() {
        responseObserver.onCompleted();
    }
}
