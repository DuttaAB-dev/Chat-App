

import com.anisala.chat.proto.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class TestEndpoints {
    public static void main(String[] args) throws Exception {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        try {
            System.out.println("=== Testing CreateUser ===");
            UserServiceGrpc.UserServiceBlockingStub userStub = UserServiceGrpc.newBlockingStub(channel);
            UserObj newUser = UserObj.newBuilder().setUserName("testuser").setName("John Doe").build();
            CreateUserResponse createRes = userStub.createUser(newUser);
            System.out.println("Response: " + createRes.getMessage());

            System.out.println("\n=== Testing LogIn ===");
            LogInRequest loginReq = LogInRequest.newBuilder().setUserName("testuser").build();
            LogInResponse loginRes = userStub.logIn(loginReq);
            System.out.println("Response: " + loginRes.getMessage());
            System.out.println("Logged in as: " + loginRes.getUser().getName());

            System.out.println("\n=== Testing ChatStream ===");
            ChatServiceGrpc.ChatServiceStub chatStub = ChatServiceGrpc.newStub(channel);
            StreamObserver<ChatMessage> requestObserver = chatStub.chatStream(new StreamObserver<ChatMessage>() {
                @Override
                public void onNext(ChatMessage msg) {
                    System.out.println("Server Response (ChatStream): " + msg.getMessage());
                }
                @Override
                public void onError(Throwable t) {
                    System.err.println("ChatStream Error: " + t.getMessage());
                }
                @Override
                public void onCompleted() {
                    System.out.println("ChatStream completed");
                }
            });
            
            requestObserver.onNext(ChatMessage.newBuilder()
                    .setSender("testuser")
                    .setReceiver("Alice")
                    .setMessage("Hello Alice! (stream)")
                    .build());
            
            Thread.sleep(1000);
            requestObserver.onCompleted();
            Thread.sleep(1000);

            System.out.println("\n=== Testing LogOut ===");
            LogOutRequest logoutReq = LogOutRequest.newBuilder().setUserId(loginRes.getUser().getUserId()).build();
            LogOutResponse logoutRes = userStub.logOut(logoutReq);
            System.out.println("Response: " + logoutRes.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            channel.shutdown();
        }
    }
}
