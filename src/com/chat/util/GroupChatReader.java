package com.chat.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import com.chat.gui.ChatWindow;
import com.chat.gui.ChatWindow.Alias;
import com.chat.gui.GroupChatWindow;

public class GroupChatReader implements Runnable {
	private DataInputStream inputStream = null;
	private Socket socket = null;
	GroupChatWindow chatBox = null;

/*	public ChatClientReader(Socket chatSocket, ChatBox chatBox) throws IOException {
		socket = chatSocket;
		inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		this.chatBox = chatBox;
		//chatBox.enableChat(chatSocket);
	}*/
	
	public GroupChatReader(Socket chatSocket, GroupChatWindow chatBox) throws SocketDisconnectException {
		try {
			socket = chatSocket;
			inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			this.chatBox = chatBox;
		} catch (IOException e) {
			throw new SocketDisconnectException();
		}
	}

	@Override
	public void run() {
		try {
			String message = inputStream.readUTF();
			do {
				System.out.println("You Received: " + message);
				chatBox.writeToDisplay(message);
			} while ((message = inputStream.readUTF()) != null);
		} catch (IOException e) {
			System.out.println("Clonnection Lost. Closing chat");
			
			// disconnect chat
			chatBox.handleDisconnect();
		}
	}

}
