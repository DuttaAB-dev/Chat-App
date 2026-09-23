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


package com.anisala.chat.server.gRPC;

import com.anisala.chat.proto.ChatMessage;
import com.anisala.chat.proto.ChatServiceGrpc;
import com.anisala.chat.server.service.ChatService;
import com.anisala.chat.server.service.SessionManager;
import io.grpc.stub.StreamObserver;

public class ChatGrpcService extends ChatServiceGrpc.ChatServiceImplBase {

    private final ChatService chatService;
    private final SessionManager sessionManager;
    private final GrpcMessagePublisher messagePublisher;

    public ChatGrpcService(
            ChatService chatService,
            SessionManager sessionManager,
            GrpcMessagePublisher messagePublisher) {
        this.chatService = chatService;
        this.sessionManager = sessionManager;
        this.messagePublisher = messagePublisher;
    }

    @Override
    public StreamObserver<ChatMessage> chatStream(StreamObserver<ChatMessage> responseObserver) {
        return new ChatStreamObserver(
                responseObserver,
                chatService,
                sessionManager,
                messagePublisher
        );
    }
}
