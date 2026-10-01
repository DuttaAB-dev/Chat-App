package com.anisala.chat.tcp;

import java.io.*;

public class MessageHandler {

    public static void writeMessage(DataOutputStream output, byte messageType, byte[] payload) throws IOException {

        int length = 1 + payload.length;

        output.writeInt(length);
        output.writeByte(messageType);
        output.write(payload);

        output.flush();
    }

    public static Message readMessage(DataInputStream input) throws IOException {

        int length = input.readInt();

        byte messageType = input.readByte();
        byte[] payload = new byte[length - 1];

        input.readFully(payload);

        return new Message(messageType, payload);
    }
}