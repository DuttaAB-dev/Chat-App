package com.anisala.chat.server.gRPC;

import com.anisala.chat.proto.ChatMessage;

import com.anisala.chat.server.model.Message;
import com.anisala.chat.server.service.MessagePublisher;

import io.grpc.stub.StreamObserver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GrpcMessagePublisher implements MessagePublisher {

    private final Map<String, StreamObserver<ChatMessage>> clientStreams = new ConcurrentHashMap<>();

    public void registerStream(String username, StreamObserver<ChatMessage> responseObserver) {
        clientStreams.put(username, responseObserver);
    }

    public void unregisterStream(String username) {
        clientStreams.remove(username);
    }

    @Override
    public boolean publish(Message domainMsg) {
        StreamObserver<ChatMessage> observer = clientStreams.get(domainMsg.getReceiverUname());
        if (observer == null) {
            return false;
        }

        ChatMessage protoMsg = ChatMessage.newBuilder()
                .setSender(domainMsg.getSenderUname())
                .setReceiver(domainMsg.getReceiverUname())
                .setMessage(domainMsg.getMessage())
                .build();

        synchronized (observer) {
            observer.onNext(protoMsg);
        }
        return true;
    }
}
