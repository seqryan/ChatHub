package com.auth.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import com.auth.beans.RemoveFriendRequest;

public class GroupParticipantThread extends Thread {
	Socket socket;
	String userName;
	DataInputStream din;
	GroupManager manager;

	public GroupParticipantThread(String userName, Socket socket, GroupManager manager) {
		super();
		this.socket = socket;
		this.userName = userName;
		this.manager = manager;
		try {
			this.din = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		} catch (IOException e) {
			// remove this from group
			manager.removeFromGroup(userName);
		}
	}
	
	@Override
	public void run() {
		while(true){
			try {
				String message = din.readUTF();
				manager.broadcaseToGroup(message);
				// broadcast message to group
			} catch (IOException e) {
				// remove this from group
				manager.removeFromGroup(userName);
				this.stop();
			}
		}
	}

}
