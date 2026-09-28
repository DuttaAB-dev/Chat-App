package com.anisala.chat.server;

import com.anisala.chat.server.gRPC.ChatGrpcEndpoint;
import com.anisala.chat.server.service.ChatService;
import com.anisala.chat.server.service.SessionManager;
import com.anisala.chat.server.service.impl.ChatServiceImpl;
import com.anisala.chat.server.service.impl.SessionManagerImpl;
import com.anisala.chat.server.repository.UserDao;
import com.anisala.chat.server.repository.impl.UserDaoImpl;
import com.anisala.chat.server.util.HibernateConfig;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.hibernate.SessionFactory;

import java.io.IOException;

public class ChatServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        
        System.out.println("Initializing Chat Server components...");

        SessionManager sessionManager = new SessionManagerImpl();
        ChatService chatService = new ChatServiceImpl(sessionManager);
        ChatGrpcEndpoint chatEndpoint = new ChatGrpcEndpoint(chatService);

        SessionFactory factory = HibernateConfig.getSessionFactory();
        UserDao userDao = new UserDaoImpl(factory);
        
        int port = 8080;
        Server server = ServerBuilder.forPort(port)
                .addService(chatEndpoint)
                .build()
                .start();

        System.out.println("Chat Server started successfully! Listening on port " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gRPC server...");
            if (server != null) {
                server.shutdown();
            }
            HibernateConfig.shutdown();
            System.out.println("Server shut down.");
        }));

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

