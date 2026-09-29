package com.anisala.chat.server.service;
import com.anisala.chat.server.model.Session;

public interface SessionManager {
    void createSession(Session session, ClientEndpoint endpoint);
    void removeSession(String userName);
    Session getSession(String userID);
    ClientEndpoint getEndpoint(String userName);
}
