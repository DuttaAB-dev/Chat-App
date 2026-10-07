package com.anisala.chat.server.model;

import java.time.Instant;

public class Message {
	private String senderId;
	private String receiverId;
	private String message;
	private Instant timestamp;
	//possibly add chat id and use proper java type for timestamp later

	public Message(String senderId, String receiverId, String message, Instant timestamp) {
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.message = message;
		this.timestamp = timestamp;
	}
	public String getSenderId() {
		return senderId;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public String getMessage() {
		return message;
	}

	public Instant getTimestamp() {
		return timestamp;
	}







}
