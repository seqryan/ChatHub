package com.chat.util;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatTextWriter {
	private DataOutputStream outputStream = null;
	private Socket socket = null;
	
	public ChatTextWriter(Socket chatSocket) {
		try {
			socket = chatSocket;
			outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		} catch (IOException e) {
			throw new SocketDisconnectException();
		}
	}
	
	public void write(String message) {
		
		try {
			outputStream.writeUTF(message);
			outputStream.flush();
		} catch (IOException e) {
			throw new SocketDisconnectException();
		}
	}
}
