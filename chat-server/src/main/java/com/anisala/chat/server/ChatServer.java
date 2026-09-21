package com.anisala.chat.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class ChatServer {

    private static final int PORT = 50051;

    private Server server;

    public void start() throws IOException {

        server = ServerBuilder
                .forPort(PORT)
                .addService(new ChatServiceImpl())
                .build()
                .start();

        System.out.println(
                "Chat server started on port " + PORT
        );
    }

    public void stop() {

        if (server != null) {
            server.shutdown();
        }
    }

    public void blockUntilShutdown()
            throws InterruptedException {

        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args)
            throws IOException, InterruptedException {

        ChatServer server = new ChatServer();

        server.start();

        server.blockUntilShutdown();
    }
}