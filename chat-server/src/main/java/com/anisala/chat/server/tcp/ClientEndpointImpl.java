package com.anisala.chat.server.tcp;

import java.io.DataOutputStream;
import java.net.Socket;
import java.time.Instant;
import java.time.ZoneOffset;

import com.anisala.chat.tcp.dto.ChatMessage;
import com.anisala.chat.tcp.MessageHandler;
import com.anisala.chat.tcp.MessageType;
import com.anisala.chat.tcp.Serialiser;
import com.anisala.chat.server.model.Message;

import com.anisala.chat.server.service.ClientEndpoint;

public class ClientEndpointImpl implements ClientEndpoint {
    private final DataOutputStream dataOutputStream;
    private final Socket socket;

    public ClientEndpointImpl(DataOutputStream dataOutputStream, Socket socket) {
        this.dataOutputStream = dataOutputStream;
        this.socket = socket;
    }

    @Override
    public void send(Message domainMsg) {
        try {
            ChatMessage dto = new ChatMessage(
                domainMsg.getSenderId(),
                domainMsg.getReceiverId(),
                domainMsg.getMessage(),
                domainMsg.getTimestamp()
            );

            byte[] payload = Serialiser.serialize(dto);
            MessageHandler.writeMessage(dataOutputStream, MessageType.MESSAGE, payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        try { socket.close(); } catch (Exception e) {}
    }
}
