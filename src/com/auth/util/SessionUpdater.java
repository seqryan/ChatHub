package com.auth.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;
import java.util.StringJoiner;

public class SessionUpdater extends Thread {
	private Connection conn;
	private Authenticator auth;

	public SessionUpdater(Connection conn, Authenticator auth) {
		this.auth = auth;
		this.conn = conn;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(5000);
			// delete closed sessions
			while (true) {
				Set<String> list = auth.getAliveSessions();
				PreparedStatement ps;
				if (null != list && !list.isEmpty()) {
					StringJoiner joiner = new StringJoiner(",");
					for (String s : list) {
						joiner.add("'" + s + "'");
					}
					ps = conn.prepareStatement("delete from session where username not in (" + joiner.toString() + ")");
				} else {
					ps = conn.prepareStatement("delete from session");
				}
				ps.executeUpdate();
				auth.clearSession();
				Thread.sleep(10000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
