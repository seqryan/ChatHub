package com.chat.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.auth.util.Client;
import com.chat.gui.ChatWindow;

public class ChatServer extends Thread {
	private ServerSocket server = null;
	Client client;
	
	public ChatServer(int port, Client client) {
		super();
		try {
			this.server = new ServerSocket(port);
			this.client = client;
		} catch (IOException e) {
			System.out.println("Error creating chat server on port " + port);
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
				// create a chat box
				new ChatWindow(appSocket, client.identify(appSocket.getInetAddress().getHostAddress()));
				//new ChatWindow(appSocket, " as ");
			} catch (IOException e) {
				System.out.println("Error creating chat instance");
			}
		}
	}

}
