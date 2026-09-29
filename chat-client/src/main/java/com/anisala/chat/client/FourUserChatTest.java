package com.anisala.chat.client;

import com.anisala.chat.proto.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;

public class FourUserChatTest {

    private static StreamObserver<ChatMessage> createResponseObserver(String user) {
        return new StreamObserver<ChatMessage>() {
            @Override
            public void onNext(ChatMessage msg) {
                // Ignore the initialization ping we send to ourselves
                if (msg.getSender().equals(msg.getReceiver())) return;
                System.out.println("[" + user + "] received message from [" + msg.getSender() + "]: " + msg.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("[" + user + "] Stream Error: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("[" + user + "] Stream closed normally.");
            }
        };
    }

    public static void main(String[] args) throws Exception {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        try {
            UserServiceGrpc.UserServiceBlockingStub userStub = UserServiceGrpc.newBlockingStub(channel);
            ChatServiceGrpc.ChatServiceStub chatStub = ChatServiceGrpc.newStub(channel);

            String[] users = {"Alice", "Bob", "Charlie", "Dave"};
            Map<String, String> userIds = new HashMap<>();

            System.out.println("=== 1. Creating and Logging In Users ===");
            for (String u : users) {
                try {
                    userStub.createUser(UserObj.newBuilder().setUserName(u).setName(u + " Name").build());
                } catch (Exception e) {
                    // Ignore if user already exists in DB
                }
                LogInResponse res = userStub.logIn(LogInRequest.newBuilder().setUserName(u).build());
                userIds.put(u, res.getUser().getUserId());
                System.out.println(u + " logged in with ID: " + res.getUser().getUserId());
            }

            System.out.println("\n=== 2. Initializing Chat Streams ===");
            StreamObserver<ChatMessage> aliceStream = chatStub.chatStream(createResponseObserver("Alice"));
            StreamObserver<ChatMessage> bobStream = chatStub.chatStream(createResponseObserver("Bob"));
            StreamObserver<ChatMessage> charlieStream = chatStub.chatStream(createResponseObserver("Charlie"));
            StreamObserver<ChatMessage> daveStream = chatStub.chatStream(createResponseObserver("Dave"));

            // IMPORTANT: The backend registers mapping endpoints *only* when a first message is sent
            // Send a ping to self to initialize session mapping in the server
            aliceStream.onNext(ChatMessage.newBuilder().setSender("Alice").setReceiver("Alice").setMessage("init").build());
            bobStream.onNext(ChatMessage.newBuilder().setSender("Bob").setReceiver("Bob").setMessage("init").build());
            charlieStream.onNext(ChatMessage.newBuilder().setSender("Charlie").setReceiver("Charlie").setMessage("init").build());
            daveStream.onNext(ChatMessage.newBuilder().setSender("Dave").setReceiver("Dave").setMessage("init").build());
            Thread.sleep(1000); // Allow server to map endpoints

            System.out.println("\n=== 3. Phase 1: Pairs (Alice <-> Bob) & (Charlie <-> Dave) ===");
            aliceStream.onNext(ChatMessage.newBuilder().setSender("Alice").setReceiver("Bob").setMessage("Hi Bob, it's Alice!").build());
            bobStream.onNext(ChatMessage.newBuilder().setSender("Bob").setReceiver("Alice").setMessage("Hello Alice! Did you get this?").build());
            charlieStream.onNext(ChatMessage.newBuilder().setSender("Charlie").setReceiver("Dave").setMessage("Hey Dave, Charlie here!").build());
            daveStream.onNext(ChatMessage.newBuilder().setSender("Dave").setReceiver("Charlie").setMessage("Loud and clear Charlie!").build());
            Thread.sleep(1500); 

            System.out.println("\n=== 4. Phase 2: Crisscross (Alice <-> Charlie) & (Bob <-> Dave) ===");
            aliceStream.onNext(ChatMessage.newBuilder().setSender("Alice").setReceiver("Charlie").setMessage("Hey Charlie, crisscross test!").build());
            charlieStream.onNext(ChatMessage.newBuilder().setSender("Charlie").setReceiver("Alice").setMessage("Nice to meet you Alice!").build());
            bobStream.onNext(ChatMessage.newBuilder().setSender("Bob").setReceiver("Dave").setMessage("Dave, how are you?").build());
            daveStream.onNext(ChatMessage.newBuilder().setSender("Dave").setReceiver("Bob").setMessage("Doing great Bob!").build());
            Thread.sleep(1500); 

            System.out.println("\n=== 5. Logging Out & Closing ===");
            for (String u : users) {
                try {
                    userStub.logOut(LogOutRequest.newBuilder().setUserId(userIds.get(u)).build());
                    System.out.println(u + " logged out via UserService.");
                } catch (Exception e) {
                    System.out.println("Could not logout " + u + ": " + e.getMessage());
                }
            }

            aliceStream.onCompleted();
            bobStream.onCompleted();
            charlieStream.onCompleted();
            daveStream.onCompleted();

            Thread.sleep(500);
            System.out.println("Test Complete!");

        } finally {
            channel.shutdown();
        }
    }
}
