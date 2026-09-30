package com.anisala.chat.server.tcp;

import java.io.*;
import java.net.Socket;

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
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) 
        {
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}