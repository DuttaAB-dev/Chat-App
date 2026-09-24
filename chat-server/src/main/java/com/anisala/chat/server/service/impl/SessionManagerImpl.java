package com.anisala.chat.server.service.impl;

import com.anisala.chat.server.model.Session;
import com.anisala.chat.server.service.SessionManager;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManagerImpl implements SessionManager {
    private final Map<String, Session> activeSessions = new ConcurrentHashMap<>();

    @Override
    public void createSession(Session session) { activeSessions.put(session.getUserName(), session); }
    @Override
    public void removeSession(String userName) { activeSessions.remove(userName); }
    @Override
    public Session getSession(String userName) { return activeSessions.get(userName); }
}