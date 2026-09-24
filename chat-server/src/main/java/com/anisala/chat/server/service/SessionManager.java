package com.anisala.chat.server.service;
import com.anisala.chat.server.model.Session;

public interface SessionManager {
    void createSession(Session session);
    void removeSession(String userName);
    Session getSession(String userName);
}




// package com.anisala.chat.server.service;
// 
// import com.anisala.chat.server.model.Session;
// 
// public interface SessionManager{
//     public Session getSession(String userName);
//     public void addSession(Session session);
//     public boolean removeSession(String userName);
//     public boolean isOnline(String userName);
// }





// import java.util.*;
// 
// import com.anisala.chat.server.model.Session;
// 
// public class SessionManager {
// 
// 	private Map<String, Session> sessionContainer = new HashMap<>();
// 	
// 	public void addSession(Session session) {
// 		sessionContainer.put(session.getUserName(), session);
// 		
// 	}
// 	
// 	public Session getSession(String userName) {
// 		Session session = sessionContainer.get(userName);
// 		return session;
// 	}
// 	
// 	public boolean removeSession(String userName) {
// 		return sessionContainer.remove(userName) != null;
// 		
// 	}
// 	
// 	public boolean isOnline(String userName) {
// 		return sessionContainer.containsKey(userName);
// 	}
// }
