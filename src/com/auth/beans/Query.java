package com.auth.beans;

public class Query {
	public static enum QueryId{
		CreateUser, Authenticate, AddFriend, RemoveFriend, GetFriends, IdentifyIP, CreateGroup, AddToGroup;
		
		public static QueryId fromString(String text) {
			if (text != null) {
				for (QueryId b : QueryId.values()) {
					if (text.equalsIgnoreCase(b.toString())) {
						return b;
					}
				}
			}
			return null;
		}
	}
	
	String queryId;
	CreateUserRequest createUserRequest;
	AuthenticateUserRequest authUserRequest;
	AddFriendRequest friendRequest ;
	RemoveFriendRequest removeFriendRequest;
	GetFriendsRequest getFriendsRequest;
	IdentifyIPRequest identifyIPRequest;
	AddGroupRequest addGroupRequest;
	
	public String getQueryId() {
		return queryId;
	}

	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}

	public CreateUserRequest getCreateUserRequest() {
		return createUserRequest;
	}

	public void setCreateUserRequest(CreateUserRequest createUserRequest) {
		this.createUserRequest = createUserRequest;
	}

	public AuthenticateUserRequest getAuthUserRequest() {
		return authUserRequest;
	}

	public void setAuthUserRequest(AuthenticateUserRequest authUserRequest) {
		this.authUserRequest = authUserRequest;
	}

	public AddFriendRequest getFriendRequest() {
		return friendRequest;
	}

	public void setFriendRequest(AddFriendRequest friendRequest) {
		this.friendRequest = friendRequest;
	}

	public GetFriendsRequest getGetFriendsRequest() {
		return getFriendsRequest;
	}

	public void setGetFriendsRequest(GetFriendsRequest getFriendsRequest) {
		this.getFriendsRequest = getFriendsRequest;
	}

	public RemoveFriendRequest getRemoveFriendRequest() {
		return removeFriendRequest;
	}

	public void setRemoveFriendRequest(RemoveFriendRequest removeFriendRequest) {
		this.removeFriendRequest = removeFriendRequest;
	}

	public IdentifyIPRequest getIdentifyIPRequest() {
		return identifyIPRequest;
	}

	public void setIdentifyIPRequest(IdentifyIPRequest identifyIPRequest) {
		this.identifyIPRequest = identifyIPRequest;
	}

	public AddGroupRequest getAddGroupRequest() {
		return addGroupRequest;
	}

	public void setAddGroupRequest(AddGroupRequest addGroupRequest) {
		this.addGroupRequest = addGroupRequest;
	}
	
}
