package com.auth.util;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GroupManager {
	String groupId;
	Map<String, DataOutputStream> members = Collections.synchronizedMap(new HashMap<String, DataOutputStream>());

	public GroupManager(String groupId) {
		super();
		this.groupId = groupId;
	}

	// add a new member to group
	public synchronized void addToGroup(String userName, String ip, int port) {
		try {
			if (!members.containsKey(userName)) {
				Socket socket = new Socket(ip, port);
				DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

				dout.writeUTF(groupId);
				dout.flush();

				members.put(userName, dout);
				Thread messageListner = new GroupParticipantThread(userName, socket, this);
				messageListner.start();
			}
		} catch (IOException e) {
			removeFromGroup(userName);
		}
	}

	// broadcast message to group
	public synchronized void broadcaseToGroup(String message) {
		System.out.println("Sending message " + message + " to group");
		;
		for (String userName : members.keySet()) {
			try {
				System.out.println("Sending " + userName);
				DataOutputStream dout = members.get(userName);
				dout.writeUTF(message);
				dout.flush();
			} catch (IOException e) {
				System.out.println("Failed to send message " + message + " to " + userName);
				// removeFromGroup(userName);
			}
		}
	}

	// remove from group
	public synchronized void removeFromGroup(String username) {
		System.out.println("Removing user " + username + " from group");
		members.remove(username);
	}
}
