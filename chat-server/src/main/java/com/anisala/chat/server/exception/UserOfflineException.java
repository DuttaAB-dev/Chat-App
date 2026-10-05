package com.anisala.chat.server.exception;

public class UserOfflineException extends RuntimeException {
    public UserOfflineException(String message) {
        super(message);
    }
}
