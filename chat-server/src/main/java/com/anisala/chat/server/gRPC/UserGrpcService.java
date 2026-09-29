package com.anisala.chat.server.gRPC;

import com.anisala.chat.proto.LogInRequest;
import com.anisala.chat.proto.LogInResponse;
import com.anisala.chat.proto.LogOutRequest;
import com.anisala.chat.proto.LogOutResponse;
// import com.anisala.chat.proto.CreateUserRequest;
import com.anisala.chat.proto.CreateUserResponse;
import com.anisala.chat.proto.UserServiceGrpc;
import com.anisala.chat.proto.UserObj;

import com.anisala.chat.server.dto.UserDto;
import com.anisala.chat.server.service.UserService;
import io.grpc.stub.StreamObserver;

public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {
	private final UserService userService;

	public UserGrpcService(UserService userService) {
		this.userService = userService;
	}

	@Override
	public void logIn(LogInRequest request, StreamObserver<LogInResponse> responseObserver) {
		String userName = request.getUserName();
		UserDto user = userService.loginUser(userName);
		if (user == null) {
			responseObserver.onError(io.grpc.Status.NOT_FOUND.withDescription("User not found").asRuntimeException());
			return;
		}
		System.out.println("User " + user.getUserName() + " logged in");

		UserObj userObj = UserObj.newBuilder()
			.setUserId(user.getUserId())
			.setName(user.getName())
			.setUserName(user.getUserName())
			.build();
			
		LogInResponse response = LogInResponse.newBuilder()
			.setUser(userObj)
			.setMessage("Logged in successfully")
			.build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
	
	@Override
	public void logOut(LogOutRequest request, StreamObserver<LogOutResponse> responseObserver) {
		String userId = request.getUserId();
		int result = userService.logoutUser(userId);
		if (result == 1) {
			responseObserver.onError(io.grpc.Status.NOT_FOUND.withDescription("User session not found").asRuntimeException());
			return;
		}
		if (result == 2) {
			responseObserver.onError(io.grpc.Status.NOT_FOUND.withDescription("User not found").asRuntimeException());
			return;
		}
		LogOutResponse response = LogOutResponse.newBuilder()
			.setMessage("Logged out successfully")
			.build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
	
	@Override
	public void createUser(UserObj request, StreamObserver<CreateUserResponse> responseObserver) {
		UserDto user = new UserDto(
			null,
			request.getUserName(),
			request.getName()
		);
		if (user == null) {
			responseObserver.onError(io.grpc.Status.INTERNAL.withDescription("Failed to create user").asRuntimeException());
			return;
		}
		userService.registerUser(user);
		
		CreateUserResponse response = CreateUserResponse.newBuilder()
			.setSuccess(true)
			.setMessage("User created successfully")
			.build();
		responseObserver.onNext(response);
		responseObserver.onCompleted(); 
	}
}
