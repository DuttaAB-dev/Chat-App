package com.anisala.chat.tcp;

public final class MessageType {
    public static final byte LOGIN = 1;
    public static final byte LOGIN_RESPONSE = 2;

    public static final byte SEND_MESSAGE = 3;
    public static final byte MESSAGE = 4;

    public static final byte GET_USER = 5;
    public static final byte USER_RESPONSE = 6;

    public static final byte UPLOAD_FILE = 7;
    public static final byte DOWNLOAD_FILE = 8;

    public static final byte FILE_CHUNK = 9;
    public static final byte FILE_COMPLETE = 10;

    public static final byte ERROR = 11;

    private MessageType() {}
}
