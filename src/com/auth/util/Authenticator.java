package com.auth.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;

public class Authenticator extends Thread {
	Connection conn = null;
	ObjectMapper mapper = new ObjectMapper();
	int serverPort;
	Map<String, Integer> sessionMap = Collections.synchronizedMap(new HashMap<String, Integer>());

	public Authenticator() {
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream("conf/chat.properties");

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			String portStr = prop.getProperty("server.port");
			if (null != portStr) {
				serverPort = Integer.parseInt(portStr);
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			String dbUser = prop.getProperty("db.user");
			String dbPasswd = prop.getProperty("db.password");
			String dbName = prop.getProperty("db.name");
			conn = DriverManager
					.getConnection("jdbc:mysql://localhost/"+ dbName +"?" + "user=" + dbUser + "&password=" + dbPasswd);

			// Do something with the Connection

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}

	@Override
	public void run() {
		try {
			ServerSocket authServer = new ServerSocket(serverPort);
			Thread sessionUpdater = new SessionUpdater(conn, this);
			sessionUpdater.start();
			while (true) {
				Socket socket = authServer.accept();
				Thread authHandler = new AuthRequestHandler(socket, conn, this);
				authHandler.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void updateSessionStatus(String userName){
		sessionMap.put(userName, 1);
	}
	
	public void clearSession(){
		sessionMap.clear();
	}
	
	public Set<String> getAliveSessions(){
		return sessionMap.keySet();
	}

	public static void main(String[] args) throws IOException {
		Authenticator auth = new Authenticator();
		auth.start();
	}
}
