package com.anisala.chat.server;

import com.anisala.chat.server.gRPC.ChatGrpcEndpoint;
import com.anisala.chat.server.service.ChatService;
import com.anisala.chat.server.service.ConnectionManager;
import com.anisala.chat.server.service.SessionManager;
import com.anisala.chat.server.service.impl.ChatServiceImpl;
import com.anisala.chat.server.service.impl.ConnectionManagerImpl;
import com.anisala.chat.server.service.impl.SessionManagerImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class ChatServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        
        System.out.println("Initializing Chat Server components...");

        // 1. Initialize the State Manager (Data Tier)
        SessionManager sessionManager = new SessionManagerImpl();

        // 2. Initialize the Transport Router
        ConnectionManager connectionManager = new ConnectionManagerImpl();

        // 3. Initialize the Core Business Logic (Service Tier)
        // Notice how we inject the two managers into the core orchestrator
        ChatService chatService = new ChatServiceImpl(sessionManager, connectionManager);

        // 4. Initialize the gRPC Adapter (Presentation Tier)
        // We inject the clean ChatService into the gRPC endpoint
        ChatGrpcEndpoint chatEndpoint = new ChatGrpcEndpoint(chatService);

        // 5. Build and Start the gRPC Server
        int port = 8080;
        Server server = ServerBuilder.forPort(port)
                .addService(chatEndpoint)
                .build()
                .start();

        System.out.println("Chat Server started successfully! Listening on port " + port);

        // 6. Handle graceful shutdown (e.g., if you press Ctrl+C)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gRPC server gracefully...");
            if (server != null) {
                server.shutdown();
            }
            System.out.println("Server shut down.");
        }));

        // 7. Block the main thread to keep the server running
        server.awaitTermination();
    }
}
// import io.grpc.Server;
// import io.grpc.ServerBuilder;
// 
// import java.io.IOException;
// 
// public class ChatServer {
// 
//     private static final int PORT = 50051;
// 
//     private Server server;
// 
//     public void start() throws IOException {
// 
//         server = ServerBuilder
//                 .forPort(PORT)
//                 .addService(new ChatServiceImpl())
//                 .build()
//                 .start();
// 
//         System.out.println(
//                 "Chat server started on port " + PORT
//         );
//     }
// 
//     public void stop() {
// 
//         if (server != null) {
//             server.shutdown();
//         }
//     }
// 
//     public void blockUntilShutdown() throws InterruptedException {
// 
//         if (server != null) {
//             server.awaitTermination();
//         }
//     }
// 
//     public static void main(String[] args) throws IOException, InterruptedException {
// 
//         ChatServer server = new ChatServer();
// 
//         server.start();
// 
//         server.blockUntilShutdown();
//         
//         Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//             System.out.println("Shutting down gRPC server...");
//             server.stop();
//         }));
//     }
// }

