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
        GrpcConnection connection = new GrpcConnection(responseObserver);
        
        return new StreamObserver<ChatMessage>() {
            private String currentUserName;

            @Override
            public void onNext(ChatMessage protoMsg) {
                if (currentUserName == null) {
                    currentUserName = protoMsg.getSender();
                    // Assumes IP is extracted from gRPC Context in a real app
                    chatService.registerUser(currentUserName, "127.0.0.1", connection);
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


// package com.anisala.chat.server.gRPC;

// import io.grpc.internal.Stream;
// import io.grpc.stub.StreamObserver;
// import com.anisala.chat.proto.ChatServiceGrpc;

// import com.anisala.chat.server.service.ChatService;


// public class ChatGrpcService extends ChatServiceGrpc.ChatServiceImplBase{
//     private ChatService chatService;

//     public ChatGrpcService(ChatService chatService) {
//         this.chatService = chatService;
//     }

//     public StreamObserver<Message> chatStream(StreamObserver<Message>   responseObserver){
//         return new StreamObserver<
//         };
//     }
// }
// 
// 
// package com.anisala.chat.server.gRPC;
// 
// import com.anisala.chat.proto.ChatMessage;
// import com.anisala.chat.proto.ChatServiceGrpc;
// import com.anisala.chat.server.service.ChatService;
// import com.anisala.chat.server.service.SessionManager;
// import io.grpc.stub.StreamObserver;
// 
// public class ChatGrpcService extends ChatServiceGrpc.ChatServiceImplBase {
// 
//     private final ChatService chatService;
//     private final SessionManager sessionManager;
//     private final GrpcMessagePublisher messagePublisher;
// 
//     public ChatGrpcService(
//             ChatService chatService,
//             SessionManager sessionManager,
//             GrpcMessagePublisher messagePublisher) {
//         this.chatService = chatService;
//         this.sessionManager = sessionManager;
//         this.messagePublisher = messagePublisher;
//     }
// 
//     @Override
//     public StreamObserver<ChatMessage> chatStream(StreamObserver<ChatMessage> responseObserver) {
//         return new ChatStreamObserver(
//                 responseObserver,
//                 chatService,
//                 sessionManager,
//                 messagePublisher
//         );
//     }
// }
