package com.anisala.chat.server.model;

import java.time.LocalDateTime;
// import java.util.function.Consumer;

// import io.grpc.stub.StreamObserver;

public class Session {
	private String userName;
	// private StreamObserver<Message> chatStream;
	private String ipAddress;
	private LocalDateTime lastActive;
	private LocalDateTime createdAt;
	// private Consumer<Message> messageListener;
	
	// public StreamObserver<Message> getChatStream() {
	// 	return chatStream;
	// }
	// public void setChatStream(StreamObserver<Message> chatStream) {
	// 	this.chatStream = chatStream;
	// }

	public Session(String userName, LocalDateTime createdAt, LocalDateTime lastActive, String ipAddress) {
		this.userName = userName;
		this.ipAddress = ipAddress;
		this.createdAt = createdAt;
		this.lastActive = lastActive;
		// this.messageListener = messageListener;
	}
	


	public String getIpAddress() {
		return ipAddress;
	}

	public String getUserName() {
		return userName;
	}

	public LocalDateTime getLastActive() {
		return lastActive;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void updateActivity() {
		this.lastActive = LocalDateTime.now();
	}
// 
// 	public Consumer<Message> getMessageListener() {
// 		return messageListener;
// 	}
// 	public void deliver(Message message) {
// 	    if(messageListener != null) {
// 	        messageListener.accept(message);
// 	    }
// 	}
}

