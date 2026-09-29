package com.anisala.chat.server.repository;

import com.anisala.chat.server.model.User;
// import com.anisala.chat.server.dto.UserDto;

public interface UserDao {
	void save(User user);
	User findByUserName(String userName);
    User findByUserID(String userId);
}