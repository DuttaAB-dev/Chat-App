package com.anisala.chat.server.service.impl;

import com.anisala.chat.server.model.User;
import com.anisala.chat.server.service.UserService;
import com.anisala.chat.server.repository.UserDao;
import com.anisala.chat.server.dto.UserDto;
import com.anisala.chat.server.service.SessionManager;
import com.anisala.chat.server.model.Session;

import java.time.Instant;

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
            return null;
		}

		return new UserDto(
			user.getUserId(),
			user.getUserName(),
			user.getName()
		);
	}

    @Override
    public UserDto getUserById(String userId) {
        User user = userDao.findByUserID(userId);
        if (user != null) {
            return new UserDto(user.getUserId(), user.getUserName(), user.getName());
        }
        return null;
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

	@Override
	public boolean isOnline(String userName) {
		User user = userDao.findByUserName(userName);
		return user != null && user.getIsOnline() != null && user.getIsOnline();
	}

	@Override
	public void setOnline(String userId, boolean isOnline) {
		User user = userDao.findByUserID(userId);
		if (user != null) {
			user.setIsOnline(isOnline);
			userDao.update(user);
		}
	}

	@Override
	public void setLastSeen(String userId, Instant lastSeen) {
		User user = userDao.findByUserID(userId);
		if (user != null) {
			user.setLastSeen(lastSeen);
			userDao.update(user);
		}
	}

	@Override
	public Instant getLastSeen(String userId) {
		User user = userDao.findByUserID(userId);
		return user != null ? user.getLastSeen() : null;
	}
}