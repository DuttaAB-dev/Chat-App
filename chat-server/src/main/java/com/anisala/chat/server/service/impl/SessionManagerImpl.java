package com.anisala.chat.server.service.impl;

import com.anisala.chat.server.model.Session;
import com.anisala.chat.server.service.ClientEndpoint;
import com.anisala.chat.server.service.SessionManager;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class ActiveSession {
    private Session session;
    private ClientEndpoint endpoint;
    
    public ActiveSession(Session session, ClientEndpoint endpoint) {
        this.session = session;
        this.endpoint = endpoint;
    }
    
    public ClientEndpoint getEndpoint() {
        return endpoint;
    }
    
    public Session getSession() {
        return session;
    }
    
}// created this to combine the session and endpoint into one object for easier storage

public class SessionManagerImpl implements SessionManager {
    private final Map<String, ActiveSession> activeSessions = new ConcurrentHashMap<>();
    @Override
    public void createSession(Session session, ClientEndpoint endpoint) {
        activeSessions.put(session.getUserId(), new ActiveSession(session, endpoint));
    }
    
    @Override
    public void removeSession(String userId) {
        activeSessions.remove(userId);
    }
    
    @Override
    public Session getSession(String userId) {
        ActiveSession activeSession = activeSessions.get(userId);// fixed nullpointer exception, triggered whern activeSessions.get(userId) gave null value
        return activeSession != null ? activeSession.getSession() : null;
    }
    
    @Override
    public ClientEndpoint getEndpoint(String userId) {
        ActiveSession activeSession = activeSessions.get(userId);// same as getSession
        return activeSession != null ? activeSession.getEndpoint() : null;
    }
}