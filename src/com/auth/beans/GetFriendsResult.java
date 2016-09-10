package com.auth.beans;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

@JsonDeserialize
public class GetFriendsResult {
	List<Friend> friends = new ArrayList<Friend>();

	public List<Friend> getFriends() {
		return friends;
	}

	public void setFriends(List<Friend> friends) {
		this.friends.addAll(friends);
	}
	
}
