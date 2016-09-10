package com.auth.util;

import com.chat.gui.ChatApp;

public class FriendListUpdater extends Thread {
	ChatApp app;

	public FriendListUpdater(ChatApp app) {
		this.app = app;
	}

	@Override
	public void run() {
		try {
			while (true) {
				System.out.println("Updating friend list");
				app.updateFriendList();
				Thread.sleep(5000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
