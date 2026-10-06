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
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public User getUser(String userName) {
        try {
            GetUserRequest req = new GetUserRequest(userName);
            byte[] payload = Serialiser.serialize(req);
            MessageHandler.writeMessage(out, MessageType.GET_USER, payload);

                        Message response = null;
            for (int i=0; i<200; i++) {
                if (ChatTcpService.lastNonChatMessage != null) {
                    response = ChatTcpService.lastNonChatMessage;
                    ChatTcpService.lastNonChatMessage = null;
                    break;
                }
                Thread.sleep(10);
            }
            if (response == null) return null;
            if (response.getMessageType() == MessageType.ERROR) return null;
            
            UserObj obj = Deserialiser.deserialize(response.getPayload(), UserObj.class);
            return new User(obj.getUserId(), obj.getUserName(), obj.getName());
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean createUser(User user) {
        try {
            UserObj obj = new UserObj(null, user.getUserName(), user.getName());
            byte[] payload = Serialiser.serialize(obj);
            
            MessageHandler.writeMessage(out, MessageType.CREATE_USER, payload); 

                        Message response = null;
            for (int i=0; i<200; i++) {
                if (ChatTcpService.lastNonChatMessage != null) {
                    response = ChatTcpService.lastNonChatMessage;
                    ChatTcpService.lastNonChatMessage = null;
                    break;
                }
                Thread.sleep(10);
            }
            if (response == null) return false;
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

    @Override
    public User getUserById(String userId) {
        try {
            GetUserRequest req = new GetUserRequest(userId); 
            byte[] payload = Serialiser.serialize(req);
            MessageHandler.writeMessage(out, MessageType.GET_USER_BY_ID, payload);

                        Message response = null;
            for (int i=0; i<200; i++) {
                if (ChatTcpService.lastNonChatMessage != null) {
                    response = ChatTcpService.lastNonChatMessage;
                    ChatTcpService.lastNonChatMessage = null;
                    break;
                }
                Thread.sleep(10);
            }
            if (response == null) return null;
            if (response.getMessageType() == MessageType.ERROR) return null;
            
            UserObj obj = Deserialiser.deserialize(response.getPayload(), UserObj.class);
            return new User(obj.getUserId(), obj.getUserName(), obj.getName());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isOnline(String userName) {
        try {
            CheckOnlineRequest req = new CheckOnlineRequest(userName);
            byte[] payload = Serialiser.serialize(req);
            MessageHandler.writeMessage(out, MessageType.CHECK_ONLINE, payload);

                        Message response = null;
            for (int i=0; i<200; i++) {
                if (ChatTcpService.lastNonChatMessage != null) {
                    response = ChatTcpService.lastNonChatMessage;
                    ChatTcpService.lastNonChatMessage = null;
                    break;
                }
                Thread.sleep(10);
            }
            if (response == null) return false;
            if (response.getMessageType() == MessageType.ERROR) return false;
            
            CheckOnlineResponse resp = Deserialiser.deserialize(response.getPayload(), CheckOnlineResponse.class);
            return resp.getIsOnline();
            
        } catch (Exception e) {
            return false;
        }
    }
}
