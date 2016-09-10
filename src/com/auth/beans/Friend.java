package com.auth.beans;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

@JsonDeserialize
public class Friend {
	String userName;
	String ip;
	int port;
	
	public Friend(){
		
	}
	
	public Friend(String userName, String ip, int port){
		this.userName = userName;
		this.ip = ip;
		this.port = port;
	}
	
	public String getUserName() {
		return userName;
	}
	public String getIp() {
		return ip;
	}
	public int getPort() {
		return port;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
}
