package com.anisala.chat.server.tcp;

import java.io.*;
import java.net.Socket;

import com.anisala.chat.tcp.Message;
import com.anisala.chat.tcp.MessageHandler;
import com.anisala.chat.tcp.MessageType;
import com.anisala.chat.tcp.Serialiser;
import com.anisala.chat.tcp.Deserialiser;
import com.anisala.chat.tcp.dto.*;

import com.anisala.chat.server.service.ClientEndpoint;
import com.anisala.chat.server.exception.UserOfflineException;


public class TcpClientHandler implements Runnable {
    private final Socket socket;
    private final ChatTcpService chatTcpService;
    private final UserTcpService userTcpService;

    public TcpClientHandler(Socket socket, ChatTcpService chatTcpService, UserTcpService userTcpService) {
        this.socket = socket;
        this.chatTcpService = chatTcpService;
        this.userTcpService = userTcpService;
    }

    @Override
    public void run() {
        String currentUserName = null;// Owner of the socket for this current thread
        
        try (InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream()) {
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            ClientEndpoint clientEndpoint = new ClientEndpointImpl(dataOutputStream, socket);
            
            while (true) {
                Message message = MessageHandler.readMessage(dataInputStream);
                byte messageType = message.getMessageType();
                byte[] payload = message.getPayload();

                switch (messageType) {
                    case MessageType.LOGIN:
                        try {
							LogInRequest loginRequest = Deserialiser.deserialize(payload, LogInRequest.class);
							userTcpService.logIn(loginRequest);
							currentUserName = loginRequest.getUserName();
							chatTcpService.startChatSession(currentUserName, clientEndpoint);
						} 
						catch (IllegalArgumentException e) {
							System.out.println("Login failed: " + e.getMessage());
							MessageHandler.writeMessage(dataOutputStream, MessageType.ERROR, e.getMessage().getBytes());
						}
                        
                        break;
                    // case MessageType.LOGIN_RESPONSE:
                    //     LoginResponse loginResponse = Deserialiser.deserialize(payload, LoginResponse.class);
                    //     
                        // break;
                    case MessageType.MESSAGE:
                        // GetUserRequest getUserRequest = Deserialiser.deserialize(payload, GetUserRequest.class);
                        // userTcpService.getUser(getUserRequest)
                        ChatMessage incomingMessage = Deserialiser.deserialize(payload, ChatMessage.class);
                        try {
                            chatTcpService.processIncomingMessage(incomingMessage); // Which calls chatService.sendMessage()
                        } catch (UserOfflineException e) {
                            System.out.println("Message failed: " + e.getMessage());
                            MessageHandler.writeMessage(
                                dataOutputStream,
                                MessageType.ERROR,
                                e.getMessage().getBytes()
                            );
                        }
                        break;
                    // case MessageType.SEND_MESSAGE:
                    //     SendMessageRequest sendMessageRequest = Deserialiser.deserialize(payload, SendMessageRequest.class);
                    //     chatTcpService.sendMessage(sendMessageRequest);
                    //     break;
                    // case MessageType.MESSAGE:
                    //     ChatMessage messageRequest = Deserialiser.deserialize(payload, ChatMessage.class);
                    //     chatTcpService.getMessage(messageRequest);
                    //     break;
                    // case MessageType.USER_RESPONSE:
                    //     UserResponse userResponse = Deserialiser.deserialize(payload, UserResponse.class);
                    //     chatTcpService.getUserResponse(userResponse);
                    //     break;
                    // case MessageType.UPLOAD_FILE:
                    //     break;
                    // case MessageType.DOWNLOAD_FILE:
                    //     break;
                    // case MessageType.FILE_CHUNK:
                    //     break;
                    // case MessageType.FILE_COMPLETE:
                    //     break;
                    // case MessageType.ERROR:
                    //     break;
                    default:
                        break;
                }
            }
        } 
        catch (java.io.IOException e) {
            System.out.println("Connection dropped for " + currentUserName + ": " + e.getMessage());
            chatTcpService.terminateChatSession(currentUserName);
            e.printStackTrace();
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            chatTcpService.terminateChatSession(currentUserName);
            System.out.println("Session terminated for " + currentUserName);
        }
    }
}