package com.anisala.chat.server.model;

public class User {
	private String userId;
	private String userName;
	private String name;

	public User(String userId, String userName, String name) {
		this.userId = userId;
		this.userName = userName;
		this.name = name;
	}

	public String getUserId() {
		return userId;
	}

	public String getName() {
		return name;
	}

	public String getUserName() {
		return userName;
	}

}
