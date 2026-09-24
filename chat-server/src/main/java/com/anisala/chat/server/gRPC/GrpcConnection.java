package com.anisala.chat.server.gRPC;

import com.anisala.chat.proto.ChatMessage;
import com.anisala.chat.server.model.Message;
import com.anisala.chat.server.service.Connection;
import com.anisala.chat.server.util.TimestampConverter;

import io.grpc.stub.StreamObserver;

public class GrpcConnection implements Connection {

    private final StreamObserver<ChatMessage> responseObserver;

    public GrpcConnection(StreamObserver<ChatMessage> responseObserver) {
        this.responseObserver = responseObserver;
    }

    @Override
    public void send(Message message) {
        // Map pure Java model to gRPC Protobuf model
        ChatMessage protoMessage = ChatMessage.newBuilder()
                .setSender(message.getSenderUname())
                .setReceiver(message.getReceiverUname())
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
