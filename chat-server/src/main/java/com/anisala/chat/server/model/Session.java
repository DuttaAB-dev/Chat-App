package com.anisala.chat.server.model;

import java.time.LocalDateTime;

public class Session {
	private String userId;
	private LocalDateTime lastActive;
	private LocalDateTime createdAt;

	public Session(String userId, LocalDateTime createdAt, LocalDateTime lastActive) {
		this.userId = userId;
		this.createdAt = createdAt;
		this.lastActive = lastActive;
	}
	


	public String getUserId() {
		return userId;
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
}

