package com.auth.beans;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

@JsonDeserialize
public class GetFriendsRequest {
	String sessionId;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
}
