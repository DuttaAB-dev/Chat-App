package com.anisala.chat.server.service.impl;

import com.anisala.chat.server.model.User;
import com.anisala.chat.server.service.UserService;
import com.anisala.chat.server.repository.UserDao;
import com.anisala.chat.server.dto.UserDto;
import com.anisala.chat.server.service.SessionManager;
import com.anisala.chat.server.model.Session;

public class UserServiceImpl implements UserService {

    private UserDao userDao;
    private SessionManager activeSessions;

    public UserServiceImpl(UserDao userDao, SessionManager activeSessions) {
        this.userDao = userDao;
        this.activeSessions = activeSessions;
    }
    
	@Override
	public void registerUser(UserDto userDto) {
		User user = new User(
			userDto.getUserName(),
			userDto.getName()
		);
		userDao.save(user);
	}

	@Override
	public UserDto loginUser(String userName) {
	    User user = userDao.findByUserName(userName);
		if (user != null) {
		    System.out.println("User logged in: " + user.getUserName());
		} else {
		    System.out.println("User not found: " + userName);
		}

		UserDto userDto = new UserDto(
			user.getUserId(),
			user.getUserName(),
			user.getName()
		);
		
		return userDto;
	}

	@Override
	public int logoutUser(String userId) {
		User user = userDao.findByUserID(userId);
		Session userSession = activeSessions.getSession(userId);
		if (user != null) {
            if (userSession == null) {
                System.out.println("User session not found for id : " + userId);
                return 1;
            }
		    
			System.out.println("User logged out: " + user.getUserName());
			return 0;
		}
		
		else {
			System.out.println("User not found: " + userId);
			return 2;
		}
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