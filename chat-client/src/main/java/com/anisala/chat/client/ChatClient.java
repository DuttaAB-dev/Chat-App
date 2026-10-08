package com.anisala.chat.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import io.grpc.*;

import com.anisala.chat.client.grpc.ChatGrpcService;
import com.anisala.chat.client.grpc.UserGrpcService;
import com.anisala.chat.client.tcp.ChatTcpService;
import com.anisala.chat.client.tcp.UserTcpService;
import com.anisala.chat.client.grpc.FileGrpcService;
import com.anisala.chat.client.presenter.ChatPresenter;
import com.anisala.chat.client.service.ChatService;
import com.anisala.chat.client.service.UserService;
import com.anisala.chat.client.view.ChatView;
import com.anisala.chat.client.view.TerminalUi;

public class ChatClient {

    private static final int TCP_PORT = 8081;
    private static final int GRPC_PORT = 8080;
    public static final String HOST = "127.0.0.1";

    private void tcpClient() throws Exception {
        System.out.println("Connecting to TCP Server at " + HOST + ":" + TCP_PORT + "...");
        Socket socket = new Socket(HOST, TCP_PORT);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try { if (!socket.isClosed()) socket.close(); } catch (Exception e) {}
        }));

        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream in = new DataInputStream(socket.getInputStream());

        UserService userService = new UserTcpService(out, in);
        ChatService chatService = new ChatTcpService(out, in);

        ChatView view = new TerminalUi();
        ChatPresenter presenter = new ChatPresenter(view, chatService, userService, null);
        presenter.start();
    }

    private void grpcClient() {
        System.out.println("Connecting to gRPC Server at " + HOST + ":" + GRPC_PORT + "...");
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(HOST, GRPC_PORT)
                .usePlaintext()
                .build();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!channel.isShutdown()) channel.shutdownNow();
        }));

        ChatView view = new TerminalUi();

        UserService userService = new UserGrpcService(channel);
        ChatService chatService = new ChatGrpcService(channel);
        FileGrpcService fileService = new FileGrpcService(channel, view);

        ChatPresenter presenter = new ChatPresenter(view, chatService, userService, fileService);
        presenter.start();
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar app.jar --tcp | --grpc");
            return;
        }

        ChatClient client = new ChatClient();
        switch (args[0]) {
            case "--tcp":
                try {
                    client.tcpClient();
                } catch (Exception e) {
                    System.err.println("Failed to connect to TCP server: " + e.getMessage());
                }
                break;
            case "--grpc":
                try {
                    client.grpcClient();
                } catch (Exception e) {
                    System.err.println("Failed to start gRPC client: " + e.getMessage());
                }
                break;
            case "--help":
                System.out.println("Usage: java -jar app.jar --tcp | --grpc");
                break;
            default:
                System.out.println("Invalid option: " + args[0]);
                System.out.println("Usage: java -jar app.jar --tcp | --grpc");
                break;
        }
    }
}
