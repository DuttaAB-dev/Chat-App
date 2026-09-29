package com.anisala.chat.server.service;

import com.anisala.chat.server.model.User;
import com.anisala.chat.server.dto.UserDto;


public interface UserService {
	void registerUser(UserDto userDto);
	UserDto loginUser(String userName);
	int logoutUser(String userId);
	void removeUser(User user);
	void updateUser(User user);
}
