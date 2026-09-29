package com.anisala.chat.server.model;

import java.time.Instant;

public class Message {
	private String senderUname;
	private String receiverUname;
	private String message;
	private Instant timestamp;
	//possibly add chat id and use proper java type for timestamp later

	public Message(String senderUname, String receiverUname, String message, Instant timestamp) {
		this.senderUname = senderUname;
		this.receiverUname = receiverUname;
		this.message = message;
		this.timestamp = timestamp;
	}
	public String getSenderUname() {
		return senderUname;
	}

	public String getReceiverUname() {
		return receiverUname;
	}

	public String getMessage() {
		return message;
	}

	public Instant getTimestamp() {
		return timestamp;
	}







}
