package com.anisala.chat.server.gRPC;

import com.anisala.chat.proto.ChatMessage;
import com.anisala.chat.proto.ChatServiceGrpc;
import com.anisala.chat.server.model.Message;
import com.anisala.chat.server.service.ChatService;
import com.anisala.chat.server.util.TimestampConverter;
import com.anisala.chat.server.exception.UserOfflineException;

import io.grpc.stub.StreamObserver;

public class ChatGrpcService extends ChatServiceGrpc.ChatServiceImplBase {
    
    private final ChatService chatService;

    public ChatGrpcService(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public StreamObserver<ChatMessage> chatStream(StreamObserver<ChatMessage> responseObserver) {
        ClientEndpointImpl endpoint = new ClientEndpointImpl(responseObserver);
        
        return new StreamObserver<ChatMessage>() {
            private String currentUserID;

            @Override
            public void onNext(ChatMessage protoMsg) {
                if (currentUserID == null) {
                    currentUserID = protoMsg.getSenderId();
                    chatService.registerUser(currentUserID, endpoint);
                }

                Message domainMessage = new Message(
                        protoMsg.getSenderId(),
                        protoMsg.getReceiverId(),
                        protoMsg.getMessage(),
                        TimestampConverter.fromProtoTimestamp(protoMsg.getTimeStamp())
                );
                try {
                        chatService.sendMessage(domainMessage);
                    } catch (UserOfflineException e) {
                        // OPTION A (Recommended for streams): Send the error back as a System message 
                        // without closing the client's connection.
                        ChatMessage errorMsg = ChatMessage.newBuilder()
                            .setSenderId("System")
                            .setMessage("[ERROR] " + e.getMessage())
                            .build();
                        responseObserver.onNext(errorMsg);
                
                        /* 
                        // OPTION B (Strict gRPC Exception): Use this if you want the stream to break 
                        // and throw StatusRuntimeException on the client side.
                        responseObserver.onError(io.grpc.Status.NOT_FOUND
                            .withDescription(e.getMessage())
                            .asRuntimeException());
                        */
                    }
            }

            @Override
            public void onError(Throwable t) { cleanup(); }
            @Override
            public void onCompleted() { cleanup(); }
            
            private void cleanup() {
                if (currentUserID != null) chatService.removeUser(currentUserID);
            }
        };
    }
}
