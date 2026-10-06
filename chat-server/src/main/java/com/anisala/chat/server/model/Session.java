package com.anisala.chat.server.model;

import java.time.Instant;

public class Session {
	private String userId;
	private Instant lastActive;
	private Instant createdAt;

	public Session(String userId, Instant createdAt, Instant lastActive) {
		this.userId = userId;
		this.createdAt = createdAt;
		this.lastActive = lastActive;
	}



	public String getUserId() {
		return userId;
	}

	public Instant getLastActive() {
		return lastActive;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void updateActivity() {
		this.lastActive = Instant.now();
	}
}

