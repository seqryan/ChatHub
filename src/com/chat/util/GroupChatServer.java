package com.chat.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.auth.util.Client;
import com.chat.gui.GroupChatWindow;

public class GroupChatServer extends Thread {
	int port;
	ServerSocket server;
	String userName;
	Client client;
	
	public GroupChatServer(String userName, int port, Client client){
		this.port = port;
		this.client = client;
		this.userName = userName;
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("Error creating group chat server on port " + port);
			this.stop();
		}
	}
	

	@Override
	public void run() {
		// accept requests for chat
		while (true) {
			try {
				Socket appSocket = server.accept();
				System.out.println("Found a new connection");
				DataInputStream din = new DataInputStream(new BufferedInputStream(appSocket.getInputStream()));
				String groupId = din.readUTF();				// create a group chat box
				System.out.println("Created group with id " + groupId);
				new GroupChatWindow(userName, appSocket, client, groupId);
			} catch (IOException e) {
				System.out.println("Error creating group chat instance");
			}
		}
	}
	

}
