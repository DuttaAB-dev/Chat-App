package com.anisala.chat.server.model;

import io.grpc.stub.StreamObserver;

public class Session {
	private String userName;
	private StreamObserver<ChatMessage> chatStream;

}
