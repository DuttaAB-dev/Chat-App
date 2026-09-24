package com.anisala.chat.server.service;


public interface ConnectionManager {
    void addConnection(String userName, Connection connection);
    void removeConnection(String userName);
    Connection getConnection(String userName);
}