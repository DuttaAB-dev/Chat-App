// src/main/java/com/anisala/chat/client/grpc/UserGrpcService.java
package com.anisala.chat.client.grpc;

import com.anisala.chat.client.model.User;
import com.anisala.chat.client.service.UserService;
import com.anisala.chat.proto.*;

import io.grpc.ManagedChannel;

public class UserGrpcService implements UserService {

    private final UserServiceGrpc.UserServiceBlockingStub userStub;
    private String currentUserName;

    public UserGrpcService(ManagedChannel channel) {
        this.userStub = UserServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public User logIn(String userName) {
        try {
            LogInResponse response = userStub.logIn(
                LogInRequest.newBuilder().setUserName(userName).build()
            );
            
            UserObj obj = response.getUser();
            this.currentUserName = obj.getUserName(); // Save local state
            
            return new User(obj.getUserId(), obj.getUserName(), obj.getName());
        } catch (Exception e) {
            System.err.println("Login failed: " + e.getMessage());
            return null;
        }
    }

    @Override
    public User getUser(String userName) {
        try {
            UserObj obj = userStub.getUser(
                GetUserRequest.newBuilder().setUserName(userName).build()
            );
            if (obj.getUserName().isEmpty()) return null;
            return new User(obj.getUserId(), obj.getUserName(), obj.getName());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean createUser(User user) {
        try {
            CreateUserResponse response = userStub.createUser(
                UserObj.newBuilder()
                    .setUserName(user.getUserName())
                    .setName(user.getName())
                    .build()
            );
            return response.getSuccess();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void logOut(String userId) {
        try {
            userStub.logOut(
                LogOutRequest.newBuilder().setUserId(userId).build()
            );
            this.currentUserName = null;
        } catch (Exception e) {
            System.err.println("Logout failed: " + e.getMessage());
        }
    }

    @Override
    public String getCurrentUserName() {
        return currentUserName;
    }

    @Override
    public User getUserById(String userId) {
        try {
            com.anisala.chat.proto.UserObj obj = userStub.getUserById(
                com.anisala.chat.proto.GetUserByIdRequest.newBuilder().setUserId(userId).build()
            );
            return new User(obj.getUserId(), obj.getUserName(), obj.getName());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean isOnline(String userName) {
        try {
            com.anisala.chat.proto.CheckOnlineResponse response = userStub.checkOnline(
                com.anisala.chat.proto.CheckOnlineRequest.newBuilder().setUserName(userName).build()
            );
            return response.getIsOnline();
        } catch (Exception e) {
            return false;
        }
    }
}
