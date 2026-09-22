package com.anisala.chat.server.model;

//message ChatMessage{
//	string sender = 1;
//	string receiver = 2;
//	string message = 3;
//}

public class ChatMessage {
	public String senderUname;
	public String receiverUname;
	public String message;
	
	
	public String getSenderUname() {
		return senderUname;
	}
	public void setSenderUname(String senderUname) {
		this.senderUname = senderUname;
	}
	public String getReceiverUname() {
		return receiverUname;
	}
	public void setReceiverUname(String receiverUname) {
		this.receiverUname = receiverUname;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
	
	
}
