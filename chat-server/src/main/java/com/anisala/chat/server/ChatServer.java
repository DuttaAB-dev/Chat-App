package com.anisala.chat.server;

import com.anisala.chat.server.gRPC.ChatGrpcService;
import com.anisala.chat.server.gRPC.UserGrpcService;

import com.anisala.chat.server.tcp.ChatTcpService;
import com.anisala.chat.server.tcp.UserTcpService;
import com.anisala.chat.server.tcp.TcpClientHandler;

import com.anisala.chat.server.service.ChatService;
import com.anisala.chat.server.service.UserService;
import com.anisala.chat.server.service.SessionManager;
import com.anisala.chat.server.service.impl.ChatServiceImpl;
import com.anisala.chat.server.service.impl.SessionManagerImpl;
import com.anisala.chat.server.service.impl.UserServiceImpl;

import com.anisala.chat.server.repository.UserDao;
import com.anisala.chat.server.repository.impl.UserDaoImpl;

import com.anisala.chat.server.util.HibernateConfig;
import io.grpc.protobuf.services.ProtoReflectionService;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import org.hibernate.SessionFactory;

import java.net.Socket;
import java.net.SocketException;
import java.net.ServerSocket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.io.IOException;

public class ChatServer {

    private static int GRPC_PORT = 8080;
    private static int TCP_PORT = 8081;

    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println("Initializing Chat Server components...");

        SessionFactory factory = HibernateConfig.getSessionFactory();
        UserDao userDao = new UserDaoImpl(factory);
        SessionManager sessionManager = new SessionManagerImpl();
        UserService userService = new UserServiceImpl(userDao, sessionManager);
        ChatService chatService = new ChatServiceImpl(sessionManager, userService);
        ChatGrpcService chatGrpcService = new ChatGrpcService(chatService);
        ChatTcpService chatTcpService = new ChatTcpService(chatService);
        UserGrpcService userGrpcService = new UserGrpcService(userService);
        UserTcpService userTcpService = new UserTcpService(userService);

        Server grpcServer = ServerBuilder.forPort(GRPC_PORT)
                .addService(chatGrpcService)
                .addService(userGrpcService)
                .addService(ProtoReflectionService.newInstance())
                .build()
                .start();

        ExecutorService clientThreadPool = Executors.newCachedThreadPool();
        ServerSocket tcpServer = new ServerSocket(TCP_PORT);

        Thread tcpThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted())
                    try{
                        Socket socket = tcpServer.accept();
                        clientThreadPool.submit(new TcpClientHandler(socket, chatTcpService, userTcpService));
                    } catch (SocketException e) {
                        System.out.println("TCP server closed.");
                        break;
                    }
            } catch (IOException e) {
                System.err.println("TCP server error: ");
                e.printStackTrace();
            }
        });
        tcpThread.start();

        System.out.println("Chat Server started successfully!\ngRPC server listening on port " + GRPC_PORT + "\nTCP server listening on port " + TCP_PORT);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gRPC server...");
            if (grpcServer != null)
                grpcServer.shutdown();

            System.out.println("Shutting down TCP server...");
            try {
                if (tcpServer != null && !tcpServer.isClosed())
                    tcpServer.close();

            } catch (IOException e) {
                System.err.println("Error closing TCP server: " + e.getMessage());
            }

            if (clientThreadPool != null)
                clientThreadPool.shutdownNow();

            HibernateConfig.shutdown();
            System.out.println("Server shut down.");
        }));

        grpcServer.awaitTermination();
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
//             server.shutdown()
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
