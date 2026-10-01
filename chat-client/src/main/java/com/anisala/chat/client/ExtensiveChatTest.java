package com.anisala.chat.client;

import com.anisala.chat.proto.ChatServiceGrpc;
import com.anisala.chat.proto.UserServiceGrpc;
import com.anisala.chat.proto.UserObj;
import com.anisala.chat.proto.LogInResponse;
import com.anisala.chat.proto.LogOutRequest;
import com.anisala.chat.tcp.Message;
import com.anisala.chat.tcp.MessageHandler;
import com.anisala.chat.tcp.MessageType;
import com.anisala.chat.tcp.Serialiser;
import com.anisala.chat.tcp.Deserialiser;
import com.anisala.chat.tcp.dto.ChatMessage;
import com.anisala.chat.tcp.dto.LogInRequest;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ExtensiveChatTest {

    interface ChatParticipant {
        void connect() throws Exception;
        void sendMessage(String to, String msg) throws Exception;
        void disconnect() throws Exception;
    }

    static class GrpcParticipant implements ChatParticipant {
        String name;
        ManagedChannel channel;
        ChatServiceGrpc.ChatServiceStub chatStub;
        StreamObserver<com.anisala.chat.proto.ChatMessage> outStream;
        
        public GrpcParticipant(String name, ManagedChannel channel) {
            this.name = name;
            this.channel = channel;
            this.chatStub = ChatServiceGrpc.newStub(channel);
        }

        @Override
        public void connect() throws Exception {
            outStream = chatStub.chatStream(new StreamObserver<com.anisala.chat.proto.ChatMessage>() {
                @Override
                public void onNext(com.anisala.chat.proto.ChatMessage msg) {
                    if (msg.getSender().equals(msg.getReceiver())) return;
                    System.out.println("[gRPC-" + name + "] received message from [" + msg.getSender() + "]: " + msg.getMessage());
                }
                @Override
                public void onError(Throwable t) {}
                @Override
                public void onCompleted() {}
            });
            // init ping
            outStream.onNext(com.anisala.chat.proto.ChatMessage.newBuilder().setSender(name).setReceiver(name).setMessage("init").build());
            Thread.sleep(500);
        }

        @Override
        public void sendMessage(String to, String msg) throws Exception {
            outStream.onNext(com.anisala.chat.proto.ChatMessage.newBuilder().setSender(name).setReceiver(to).setMessage(msg).build());
        }

        @Override
        public void disconnect() throws Exception {
            if (outStream != null) outStream.onCompleted();
        }
    }

    static class TcpParticipant implements ChatParticipant {
        String name;
        Socket socket;
        DataOutputStream dos;
        DataInputStream dis;
        Thread listener;
        volatile boolean running = false;

        public TcpParticipant(String name) {
            this.name = name;
        }

        @Override
        public void connect() throws Exception {
            socket = new Socket("localhost", 8081);
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            running = true;

            byte[] payload = Serialiser.serialize(new com.anisala.chat.tcp.dto.LogInRequest(name));
            MessageHandler.writeMessage(dos, MessageType.LOGIN, payload);

            listener = new Thread(() -> {
                try {
                    while (running && !socket.isClosed()) {
                        Message msg = MessageHandler.readMessage(dis);
                        if (msg.getMessageType() == MessageType.MESSAGE) {
                            ChatMessage chatMsg = Deserialiser.deserialize(msg.getPayload(), ChatMessage.class);
                            if (chatMsg != null && (!chatMsg.getSender().equals(chatMsg.getReceiver()) || chatMsg.getMessage().equals("init"))) {
                                if (chatMsg.getSender().equals(chatMsg.getReceiver())) continue;
                                System.out.println("[TCP-" + name + "] received message from [" + chatMsg.getSender() + "]: " + chatMsg.getMessage());
                            }
                        }
                    }
                } catch (Exception e) {
                    if (running) System.err.println("[TCP-" + name + "] socket closed.");
                }
            });
            listener.start();
            Thread.sleep(500); // Give server time to setup session
        }

        @Override
        public void sendMessage(String to, String msg) throws Exception {
            ChatMessage chatMsg = new ChatMessage(name, to, msg, LocalDateTime.now());
            byte[] payload = Serialiser.serialize(chatMsg);
            MessageHandler.writeMessage(dos, MessageType.MESSAGE, payload);
        }

        @Override
        public void disconnect() throws Exception {
            running = false;
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (listener != null) listener.interrupt();
        }
    }

    public static void main(String[] args) throws Exception {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 8080)
                .usePlaintext()
                .build();
        
        com.anisala.chat.proto.UserServiceGrpc.UserServiceBlockingStub userStub = UserServiceGrpc.newBlockingStub(channel);
        String[] users = {"Alice", "Bob", "Charlie", "Dave"};
        Map<String, String> userIds = new HashMap<>();

        // Create Users once
        for (String u : users) {
             try {
                 userStub.createUser(UserObj.newBuilder().setUserName(u).setName(u + " Name").build());
             } catch (Exception e) {
                 // Ignore if exists
             }
        }
        
        runScenario("Scenario 1: All 4 TCP", false, false, false, false, channel, userStub, users, userIds);
        runScenario("Scenario 2: All 4 gRPC", true, true, true, true, channel, userStub, users, userIds);
        runScenario("Scenario 3: Half TCP, Half gRPC (Alice,Bob=gRPC, Charlie,Dave=TCP)", true, true, false, false, channel, userStub, users, userIds);

        channel.shutdown();
        System.exit(0);
    }

    public static void runScenario(String scenarioName, boolean p1Grpc, boolean p2Grpc, boolean p3Grpc, boolean p4Grpc, ManagedChannel channel, com.anisala.chat.proto.UserServiceGrpc.UserServiceBlockingStub userStub, String[] users, Map<String, String> userIds) throws Exception {
        System.out.println("\n=======================================================");
        System.out.println(scenarioName);
        System.out.println("=======================================================\n");

        // Login at different times
        for (String u : users) {
            com.anisala.chat.proto.LogInRequest req = com.anisala.chat.proto.LogInRequest.newBuilder().setUserName(u).build();
            LogInResponse res = userStub.logIn(req);
            userIds.put(u, res.getUser().getUserId());
            System.out.println(u + " logged in via gRPC UserService.");
            Thread.sleep(300); // staggered login
        }

        ChatParticipant p1 = p1Grpc ? new GrpcParticipant("Alice", channel) : new TcpParticipant("Alice");
        ChatParticipant p2 = p2Grpc ? new GrpcParticipant("Bob", channel) : new TcpParticipant("Bob");
        ChatParticipant p3 = p3Grpc ? new GrpcParticipant("Charlie", channel) : new TcpParticipant("Charlie");
        ChatParticipant p4 = p4Grpc ? new GrpcParticipant("Dave", channel) : new TcpParticipant("Dave");

        p1.connect(); p2.connect(); p3.connect(); p4.connect();

        System.out.println("\n--- Phase 1: Pairs (Alice <-> Bob) & (Charlie <-> Dave) ---");
        p1.sendMessage("Bob", "Hello Bob, are you receiving this?");
        p2.sendMessage("Alice", "Yes Alice, getting it fine!");
        Thread.sleep(1000); // add delay to ensure ordering
        p3.sendMessage("Dave", "Hi Dave, what protocol are you on?");
        p4.sendMessage("Charlie", "Doesn't matter, it's all routed!");
        Thread.sleep(2000);

        System.out.println("\n--- Phase 2: Crisscross (Alice <-> Charlie) & (Bob <-> Dave) ---");
        p1.sendMessage("Charlie", "Charlie, switching it up!");
        p3.sendMessage("Alice", "Hello Alice, I see you!");
        Thread.sleep(1000);
        p2.sendMessage("Dave", "Dave! It's Bob.");
        p4.sendMessage("Bob", "Loud and clear Bob.");
        
        Thread.sleep(2000);
        
        System.out.println("\n--- Logging out ---");
        // Staggered disconnect / logout
        ChatParticipant[] parts = {p1, p2, p3, p4};
        for (int i=0; i<4; i++) {
            parts[i].disconnect();
            try { userStub.logOut(LogOutRequest.newBuilder().setUserId(userIds.get(users[i])).build()); } catch(Exception e) {}
            System.out.println(users[i] + " disconnected and logged out.");
            Thread.sleep(300);
        }
        Thread.sleep(1000);
    }
}
