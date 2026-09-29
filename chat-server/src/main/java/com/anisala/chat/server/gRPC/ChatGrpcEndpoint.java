package com.anisala.chat.server.gRPC;

import com.anisala.chat.proto.ChatMessage;
import com.anisala.chat.proto.ChatServiceGrpc;
import com.anisala.chat.server.model.Message;
import com.anisala.chat.server.service.ChatService;
import com.anisala.chat.server.util.TimestampConverter;

import io.grpc.stub.StreamObserver;

public class ChatGrpcEndpoint extends ChatServiceGrpc.ChatServiceImplBase {
    
    private final ChatService chatService;

    public ChatGrpcEndpoint(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public StreamObserver<ChatMessage> chatStream(StreamObserver<ChatMessage> responseObserver) {
        ClientEndpointImpl endpoint = new ClientEndpointImpl(responseObserver);
        
        return new StreamObserver<ChatMessage>() {
            private String currentUserName;

            @Override
            public void onNext(ChatMessage protoMsg) {
                if (currentUserName == null) {
                    currentUserName = protoMsg.getSender();
                    chatService.registerUser(currentUserName, endpoint);
                }

                Message domainMessage = new Message(
                        protoMsg.getSender(),
                        protoMsg.getReceiver(),
                        protoMsg.getMessage(),
                        TimestampConverter.fromProtoTimestamp(protoMsg.getTimeStamp())
                );
                chatService.sendMessage(domainMessage);
            }

            @Override
            public void onError(Throwable t) { cleanup(); }
            @Override
            public void onCompleted() { cleanup(); }
            
            private void cleanup() {
                if (currentUserName != null) chatService.removeUser(currentUserName);
            }
        };
    }
}
