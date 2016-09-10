package com.auth.beans;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

@JsonDeserialize
public class RemoveFriendRequest {
	String sessionId;
	String friendName;

	public String getFriendName() {
		return friendName;
	}

	public void setFriendName(String friendName) {
		this.friendName = friendName;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
