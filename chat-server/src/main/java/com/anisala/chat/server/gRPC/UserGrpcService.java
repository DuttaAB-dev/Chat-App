package com.anisala.chat.server.gRPC;

import com.anisala.chat.proto.LogInRequest;
import com.anisala.chat.proto.LogInResponse;
import com.anisala.chat.proto.LogOutRequest;
import com.anisala.chat.proto.LogOutResponse;
// import com.anisala.chat.proto.CreateUserRequest;
import com.anisala.chat.proto.CreateUserResponse;
import com.anisala.chat.proto.GetLastSeenRequest;
import com.anisala.chat.proto.GetLastSeenResponse;
import com.anisala.chat.proto.UserServiceGrpc;
import com.anisala.chat.proto.UserObj;
import com.anisala.chat.proto.CheckOnlineRequest;
import com.anisala.chat.proto.CheckOnlineResponse;
import com.anisala.chat.proto.UpdateLastSeenRequest;
import com.anisala.chat.proto.UpdateLastSeenResponse;

import com.anisala.chat.server.dto.UserDto;
import com.anisala.chat.server.service.UserService;
import io.grpc.stub.StreamObserver;

import java.time.Instant;
import com.anisala.chat.server.util.TimestampConverter;

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

	@Override
	public void getLastSeen(GetLastSeenRequest request, StreamObserver<GetLastSeenResponse> responseObserver) {
		String userName = request.getUserId();
		Instant lastSeen = userService.getLastSeen(userName);
		GetLastSeenResponse response = GetLastSeenResponse.newBuilder()
			.setLastSeen(TimestampConverter.toProtoTimestamp(lastSeen))
			.build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void checkOnline(CheckOnlineRequest request, StreamObserver<CheckOnlineResponse> responseObserver) {
		String userName = request.getUserName();
		boolean isOnline = userService.isOnline(userName);
		CheckOnlineResponse response = CheckOnlineResponse.newBuilder()
			.setIsOnline(isOnline)
			.build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void updateLastSeen(UpdateLastSeenRequest request, StreamObserver<UpdateLastSeenResponse> responseObserver) {
		String userId = request.getUserId();
		Instant lastSeen = TimestampConverter.fromProtoTimestamp(request.getLastSeen());
		userService.setLastSeen(userId, lastSeen);
		UpdateLastSeenResponse response = UpdateLastSeenResponse.newBuilder()
			.setMessage("Last seen updated successfully")
			.build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void getUser(com.anisala.chat.proto.GetUserRequest request, StreamObserver<com.anisala.chat.proto.UserObj> responseObserver) {
		String userName = request.getUserName();
		UserDto user = userService.loginUser(userName);
		if (user == null) {
			responseObserver.onNext(com.anisala.chat.proto.UserObj.newBuilder().build());
		} else {
			responseObserver.onNext(com.anisala.chat.proto.UserObj.newBuilder()
				.setUserId(user.getUserId())
				.setName(user.getName())
				.setUserName(user.getUserName())
				.build());
		}
		responseObserver.onCompleted();
	}

	@Override
	public void getUserById(com.anisala.chat.proto.GetUserByIdRequest request, StreamObserver<com.anisala.chat.proto.UserObj> responseObserver) {
		String userId = request.getUserId();
		UserDto user = userService.getUserById(userId);
		if (user == null) {
			responseObserver.onNext(com.anisala.chat.proto.UserObj.newBuilder().build());
		} else {
			responseObserver.onNext(com.anisala.chat.proto.UserObj.newBuilder()
				.setUserId(user.getUserId())
				.setName(user.getName())
				.setUserName(user.getUserName())
				.build());
		}
		responseObserver.onCompleted();
	}
}
