package com.auth.beans;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

@JsonDeserialize
public class AddGroupRequest {
	String userName;
	String groupId;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
}
