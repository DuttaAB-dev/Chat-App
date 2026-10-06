package com.anisala.chat.server.repository;

import com.anisala.chat.server.model.User;

import java.time.Instant;

public interface UserDao {
	void save(User user);
	User findByUserName(String userName);
    User findByUserID(String userId);
    // boolean isOnline(String userId);
    // void setOnline(String userId, boolean isOnline);
    // void setLastSeen(String userId, Instant lastSeen);
    // Instant getLastSeen(String userId);
    User update(User user);
}