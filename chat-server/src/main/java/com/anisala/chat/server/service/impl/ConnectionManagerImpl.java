package com.anisala.chat.server.service.impl;

import com.anisala.chat.server.service.Connection;
import com.anisala.chat.server.service.ConnectionManager;

import java.util.Map;
import java.util.HashMap;

public class ConnectionManagerImpl implements ConnectionManager {
    
    // Thread-safe map to hold active user ports
    private final Map<String, Connection> activeConnections = new HashMap<>();

    @Override
    public void addConnection(String userName, Connection connection) {
        activeConnections.put(userName, connection);
    }

    @Override
    public void removeConnection(String userName) {
        activeConnections.remove(userName);
    }

    @Override
    public Connection getConnection(String userName) {
        return activeConnections.get(userName);
    }
}