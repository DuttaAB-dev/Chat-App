package com.anisala.chat.server.service;

import com.anisala.chat.server.model.User;

public interface UserService {
	void registerUser(User user);
	User loginUser(String userName);
	void removeUser(User user);
	void updateUser(User user);
}
