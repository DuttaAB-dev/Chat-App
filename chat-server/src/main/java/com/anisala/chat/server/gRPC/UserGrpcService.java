package com.anisala.chat.server.gRPC;

import com.anisala.chat.proto.LogInRequest;
import com.anisala.chat.proto.LogInResponse;
import com.anisala.chat.server.model.User;
// import com.anisala.chat.proto.User;
import com.anisala.chat.server.service.UserService;
import io.grpc.stub.StreamObserver;

public class UserGrpcService {
	private final UserService userService;

	public UserGrpcService(UserService userService) {
		this.userService = userService;
	}

	public void logIn(LogInRequest request, StreamObserver<LogInResponse> responseObserver) {
		String userName = request.getUserName();
		User user = userService.loginUser(userName);
		
		LogInResponse response = LogInResponse.newBuilder().build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
}
