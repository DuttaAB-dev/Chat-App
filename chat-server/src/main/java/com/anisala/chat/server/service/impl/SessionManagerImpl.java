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
    
}

public class SessionManagerImpl implements SessionManager {
    private final Map<String, ActiveSession> activeSessions = new ConcurrentHashMap<>();
    @Override
    public void createSession(Session session, ClientEndpoint endpoint) {
        activeSessions.put(session.getUserName(), new ActiveSession(session, endpoint));
    }
    
    @Override
    public void removeSession(String userName) {
        activeSessions.remove(userName);
    }
    
    @Override
    public Session getSession(String userName) {
        return activeSessions.get(userName).getSession();
    }
    
    @Override
    public ClientEndpoint getEndpoint(String userName) {
        return activeSessions.get(userName).getEndpoint();
    }
}