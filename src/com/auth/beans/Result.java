package com.auth.beans;

public class Result {
	boolean success;
	String errorMessage;
	
	IdentifyIPResult identifyIPResult;
	AuthenticateUserResult authUserResult;
	GetFriendsResult getFriendsResult;
	CreateGroupResult createGroupResult;
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public AuthenticateUserResult getAuthUserResult() {
		return authUserResult;
	}
	public void setAuthUserResult(AuthenticateUserResult authUserResult) {
		this.authUserResult = authUserResult;
	}
	public GetFriendsResult getGetFriendsResult() {
		return getFriendsResult;
	}
	public void setGetFriendsResult(GetFriendsResult getFriendsResult) {
		this.getFriendsResult = getFriendsResult;
	}
	public IdentifyIPResult getIdentifyIPResult() {
		return identifyIPResult;
	}
	public void setIdentifyIPResult(IdentifyIPResult identifyIPResult) {
		this.identifyIPResult = identifyIPResult;
	}
	public CreateGroupResult getCreateGroupResult() {
		return createGroupResult;
	}
	public void setCreateGroupResult(CreateGroupResult createGroupResult) {
		this.createGroupResult = createGroupResult;
	}
}
