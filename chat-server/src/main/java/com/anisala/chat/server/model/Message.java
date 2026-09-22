package com.anisala.chat.server.model;

//message ChatMessage{
//	string sender = 1;
//	string receiver = 2;
//	string message = 3;
//}



public class Message {
	private String senderUname;
	private String receiverUname;
	private String message;
	private String timestamp;
	//possibly add chat id and use proper java type for timestamp later
	
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
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	
	
	
	
	
}
