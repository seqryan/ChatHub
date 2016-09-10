package com.auth.beans;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

@JsonDeserialize
public class CreateGroupResult {

	String groupId;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
}
