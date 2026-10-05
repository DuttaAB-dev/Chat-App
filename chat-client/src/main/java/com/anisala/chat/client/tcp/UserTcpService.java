package com.anisala.chat.client.tcp;

import com.anisala.chat.client.model.User;
import com.anisala.chat.client.service.UserService;
import com.anisala.chat.tcp.*;
import com.anisala.chat.tcp.dto.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class UserTcpService implements UserService {

    private final DataOutputStream out;
    private final DataInputStream in;
    private String currentUserName;

    public UserTcpService(DataOutputStream out, DataInputStream in) {
        this.out = out;
        this.in = in;
    }

    @Override
    public User logIn(String userName) {
        try {
            LogInRequest loginReq = new LogInRequest(userName);
            byte[] payload = Serialiser.serialize(loginReq);
            MessageHandler.writeMessage(out, MessageType.LOGIN, payload);

            // Wait for synchronous response from server
            Message response = MessageHandler.readMessage(in);
            if (response.getMessageType() == MessageType.ERROR) {
                System.err.println("Login failed: " + new String(response.getPayload()));
                return null;
            }
            
            LogInResponse loginResp = Deserialiser.deserialize(response.getPayload(), LogInResponse.class);
            UserObj obj = loginResp.getUser();
            
            this.currentUserName = obj.getUserName(); // Save local state
            return new User(obj.getUserId(), obj.getUserName(), obj.getName());
            
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public User getUser(String userName) {
        try {
            GetUserRequest req = new GetUserRequest(userName);
            byte[] payload = Serialiser.serialize(req);
            MessageHandler.writeMessage(out, MessageType.GET_USER, payload);

            Message response = MessageHandler.readMessage(in);
            if (response.getMessageType() == MessageType.ERROR) return null;
            
            UserObj obj = Deserialiser.deserialize(response.getPayload(), UserObj.class);
            return new User(obj.getUserId(), obj.getUserName(), obj.getName());
            
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean createUser(User user) {
        try {
            UserObj obj = new UserObj(null, user.getUserName(), user.getName());
            byte[] payload = Serialiser.serialize(obj);
            
            // Assuming CREATE_USER is handled by GET_USER for now, update if you add a new MessageType
            MessageHandler.writeMessage(out, MessageType.GET_USER, payload); 

            Message response = MessageHandler.readMessage(in);
            if (response.getMessageType() == MessageType.ERROR) return false;
            
            CreateUserResponse resp = Deserialiser.deserialize(response.getPayload(), CreateUserResponse.class);
            return resp.isSuccess();
            
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void logOut(String userId) {
        try {
            LogOutRequest req = new LogOutRequest(userId);
            byte[] payload = Serialiser.serialize(req);
            // Assuming LOGOUT is handled by LOGIN or a new MessageType you add later
            MessageHandler.writeMessage(out, MessageType.LOGIN, payload); 
            
            this.currentUserName = null;
        } catch (Exception e) {
            System.err.println("Logout failed: " + e.getMessage());
        }
    }

    @Override
    public String getCurrentUserName() {
        return currentUserName;
    }
}
