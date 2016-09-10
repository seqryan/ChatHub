package com.chat.util;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ChatClientWriter implements Runnable {
	private DataOutputStream outputStream = null;
	private Socket socket = null;

	public ChatClientWriter(Socket chatSocket) throws IOException {
		socket = chatSocket;
		outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
	}

	@Override
	public void run() {
		Scanner reader = new Scanner(System.in);

		try {
			while (true) {
				outputStream.writeUTF(reader.nextLine());
				outputStream.flush();
				
			}
		} catch (IOException e) {
			System.out.println("Clonnection Lost. Closing chat");
			throw new SocketDisconnectException();
		}
	}
}
