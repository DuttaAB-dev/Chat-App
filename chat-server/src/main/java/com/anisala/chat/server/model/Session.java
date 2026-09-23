package com.anisala.chat.server.model;

import java.time.LocalDateTime;
import java.util.function.Consumer;

// import io.grpc.stub.StreamObserver;

public class Session {
	private String userName;
	// private StreamObserver<Message> chatStream;
	private LocalDateTime lastActive;
	private LocalDateTime createdAt;
	private Consumer<Message> messageListener;
	
	// public StreamObserver<Message> getChatStream() {
	// 	return chatStream;
	// }
	// public void setChatStream(StreamObserver<Message> chatStream) {
	// 	this.chatStream = chatStream;
	// }

	public Session(String userName, LocalDateTime lastActive, LocalDateTime createdAt, Consumer<Message> messageListener) {
		this.userName = userName;
		this.lastActive = lastActive;
		this.createdAt = createdAt;
		this.messageListener = messageListener;
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

	public Consumer<Message> getMessageListener() {
		return messageListener;
	}
	public void deliver(Message message) {
	    if(messageListener != null) {
	        messageListener.accept(message);
	    }
	}
}

