package com.anisala.chat.server.model;

import io.grpc.stub.StreamObserver;

public class Session {
	private String userName;
	private StreamObserver<Message> chatStream;
	
	
	public StreamObserver<Message> getChatStream() {
		return chatStream;
	}
	public void setChatStream(StreamObserver<Message> chatStream) {
		this.chatStream = chatStream;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

}
