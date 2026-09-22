package com.anisala.chat.server.service;


import java.util.*;

import com.anisala.chat.server.model.Session;

public class SessionManager {

	private Map<String, Session> sessionContainer = new HashMap();
	
	public void addSession(Session session) {
		sessionContainer.put(session.getUserName(), session);
		
	}
	
	public Session getSession(String userName) {
		Session session = sessionContainer.get(userName);
		return session;
	}
	
	public boolean removeSession(String userName) {
		try {
			sessionContainer.remove(userName);
			return true;
		}
		
		catch(Exception e){
			return false;
		}
	}
	
	public boolean isOnline(String userName) {
		return sessionContainer.containsKey(userName);
	}
}
