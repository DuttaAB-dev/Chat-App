package com.anisala.chat.server.tcp;

import com.anisala.chat.tcp.dto.LogInRequest;
import com.anisala.chat.tcp.dto.LogInResponse;
import com.anisala.chat.tcp.dto.LogOutRequest;
import com.anisala.chat.tcp.dto.LogOutResponse;
import com.anisala.chat.tcp.dto.CreateUserResponse;
import com.anisala.chat.tcp.dto.UserObj;

import com.anisala.chat.server.dto.UserDto;
import com.anisala.chat.server.service.UserService;

public class UserTcpService {

    private final UserService userService;

    public UserTcpService(UserService userService) {
        this.userService = userService;
    }
    
    public UserDto getUserById(String userId) {
        return userService.getUserById(userId);
    }
    
    public boolean isOnline(String userName) {
        return userService.isOnline(userName);
    }
    
    public UserDto getUser(String userName) {
        return userService.loginUser(userName); // Using loginUser as standard fetch for now
    }

    public LogInResponse logIn(LogInRequest request) {
        String userName = request.getUserName();
        UserDto user = userService.loginUser(userName);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        System.out.println("User " + user.getUserName() + " logged in");
      
		UserObj userObj = new UserObj(
			user.getUserId(),
			user.getUserName(),
			user.getName()
		);
			
		LogInResponse response = new LogInResponse(
			userObj,
			"Logged in successfully"
		);
		return response;
    }
    
    public LogOutResponse logOut(LogOutRequest request) {
		String userId = request.getUserId();
		int result = userService.logoutUser(userId);
		if (result == 1) {
			throw new IllegalArgumentException("User session not found");
		}
		if (result == 2) {
			throw new IllegalArgumentException("User not found");
		}
		
		LogOutResponse response = new LogOutResponse("Logged out successfully");
		return response;
	}
	
	public CreateUserResponse createUser(UserObj request) {
		UserDto user = new UserDto(
			null,
			request.getUserName(),
			request.getName()
		);
		if (user == null) {
			throw new IllegalArgumentException("Failed to create user");
		}
		userService.registerUser(user);
		
		CreateUserResponse response = new CreateUserResponse(
		    true, 
		    "User created successfully"
		);
		return response;
	}
}