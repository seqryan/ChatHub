package com.chat.impl;

import com.auth.util.Authenticator;
import com.chat.gui.ChatApp;

public class Runner {
	public static void main(String[] args) {
		switch(args[0]){
		case "server":
			System.out.println("Starting authentication server");
			Authenticator auth = new Authenticator();
			auth.start();
			break;
		case "client":
			System.out.println("Starting chatting application");
			ChatApp app = new ChatApp();
			break;
		}
	}
}
