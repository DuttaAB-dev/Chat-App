package com.anisala.chat.server.service.impl;

import com.anisala.chat.server.model.User;
import com.anisala.chat.server.service.UserService;
import com.anisala.chat.server.repository.UserDao;

public class UserServiceImpl implements UserService {

    private UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }
    
	@Override
	public void registerUser(User user) {
		
	}

	@Override
	public User loginUser(String userName) {
	    User user = userDao.findByUserName(userName);
		if (user != null) {
		    System.out.println("User logged in: " + user.getUserName());
		} else {
		    System.out.println("User not found: " + userName);
		}
		return user;
	}

	@Override
	public void removeUser(User user) {
		// will implement later
	}

	@Override
	public void updateUser(User user) {
		// for future if this project continues
	}
}