package com.anisala.chat.server.service;

import com.anisala.chat.server.model.User;

import java.time.Instant;

import com.anisala.chat.server.dto.UserDto;


public interface UserService {
	void registerUser(UserDto userDto);
	UserDto loginUser(String userName);
	int logoutUser(String userId);
	void removeUser(User user);
	void updateUser(User user);
    UserDto getUserById(String userId);
	boolean isOnline(String userName);
	void setOnline(String userId, boolean isOnline);
	void setLastSeen(String userId, Instant lastSeen);
	Instant getLastSeen(String userId);
}
