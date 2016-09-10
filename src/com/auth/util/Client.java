package com.auth.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.map.ObjectMapper;

import com.auth.beans.AddFriendRequest;
import com.auth.beans.AddGroupRequest;
import com.auth.beans.AuthenticateUserRequest;
import com.auth.beans.AuthenticateUserResult;
import com.auth.beans.CreateUserRequest;
import com.auth.beans.Friend;
import com.auth.beans.GetFriendsRequest;
import com.auth.beans.IdentifyIPRequest;
import com.auth.beans.Query;
import com.auth.beans.Query.QueryId;
import com.auth.beans.RemoveFriendRequest;
import com.auth.beans.Result;
import com.chat.gui.ChatApp;
import com.chat.util.ChatServer;
import com.chat.util.GroupChatServer;

public class Client {
	Socket socket = null;
	ObjectMapper mapper = new ObjectMapper();
	DataInputStream inputStream;
	DataOutputStream outputStream;
	String ip;
	String serverIp;
	int port;
	int groupport;
	int serverPort;
	String userName;

	public Client(){
		try {
			InetAddress addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress();
			
			Properties prop = new Properties();
			InputStream input = null;
			
			try {

				input = new FileInputStream("conf/chat.properties");

				// load a properties file
				prop.load(input);

				String portStr = prop.getProperty("server.port");
				if(null != portStr){
					serverPort = Integer.parseInt(portStr);
				}
				String serverIp = prop.getProperty("server.ip");
				
				portStr = prop.getProperty("client.port");
				if(null != portStr && checkPort(portStr)){
					port = Integer.parseInt(portStr);
				}
				
				portStr = prop.getProperty("client.group.port");
				if(null != portStr && checkPort(portStr)){
					groupport = Integer.parseInt(portStr);
				}
				
				// start chat app
				//ChatApp app = new ChatApp();
				
				socket = new Socket(serverIp, serverPort);
				inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

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
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public String getUserName() {
		return userName;
	}

	public void startServerThreads(String userName) {
		// start chat request listener
		Thread server = new ChatServer(port, this);
		server.start();
		
		// start group chat request listener
		server = new GroupChatServer(userName, groupport, this);
		server.start();
	}
	
	public boolean checkPort(String portStr){
		Pattern portPattern = Pattern.compile("^[0-9]+$");
		Matcher portMatch = portPattern.matcher(portStr);
		int port;
		if (!portMatch.find()) {
			System.out.println("Invalid port number");
			return false;
		} else {
			port = Integer.parseInt(portStr);
			if (port < 0 || port > 65535) {
				System.out.println("Invalid port number");
				return false;
			}
		}
		
		return true;
	}
	
	public String createUser(String userName, String password) {
		String error = null;
		CreateUserRequest request = new CreateUserRequest(userName, password);
		Query query = new Query();
		query.setQueryId(QueryId.CreateUser.toString());
		query.setCreateUserRequest(request);

		// System.out.println(mapper.writeValueAsString(query));

		try {
			outputStream.writeUTF(mapper.writeValueAsString(query));
			outputStream.flush();

			String resStr = inputStream.readUTF();
			Result result = mapper.readValue(resStr, Result.class);
			if (!result.isSuccess()) {
				error = result.getErrorMessage();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return error;
	}
	
	public String authenticate(String userName, String password){
		String sessionId = null;
		
		AuthenticateUserRequest authReq = new AuthenticateUserRequest();
		authReq.setUserName(userName);
		authReq.setPassword(password);
		authReq.setIp(ip);
		authReq.setPort(String.valueOf(port));
		authReq.setGroupport(String.valueOf(groupport));
		
		Query query = new Query();
		query.setQueryId(QueryId.Authenticate.toString());
		query.setAuthUserRequest(authReq);
		
		try {
			outputStream.writeUTF(mapper.writeValueAsString(query));
			outputStream.flush();

			String resStr = inputStream.readUTF();
			Result result = mapper.readValue(resStr, Result.class);
			if (result.isSuccess()) {
				AuthenticateUserResult authRes = result.getAuthUserResult();
				sessionId = authRes.getSessionId();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sessionId;
	}
	
	public boolean addFriend(String sessionId, String friendName){
		boolean added = false;
		AddFriendRequest friendReq = new AddFriendRequest();
		friendReq.setSessionId(sessionId);
		friendReq.setFriendName(friendName);
		
		Query query = new Query();
		query.setQueryId(QueryId.AddFriend.toString());
		query.setFriendRequest(friendReq);
		
		try {
			outputStream.writeUTF(mapper.writeValueAsString(query));
			outputStream.flush();

			String resStr = inputStream.readUTF();
			Result result = mapper.readValue(resStr, Result.class);
			added = result.isSuccess();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return added;
	}
	
	public String identify(String ip){
		String userName = null;
		IdentifyIPRequest identifyReq = new IdentifyIPRequest();
		identifyReq.setIp(ip);
		
		Query query = new Query();
		query.setQueryId(QueryId.IdentifyIP.toString());
		query.setIdentifyIPRequest(identifyReq);
		
		try {
			outputStream.writeUTF(mapper.writeValueAsString(query));
			outputStream.flush();

			String resStr = inputStream.readUTF();
			Result result = mapper.readValue(resStr, Result.class);
			if(result.isSuccess()){
				userName = result.getIdentifyIPResult().getUserName();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return userName;
	}
	
	public boolean removeFriend(String sessionId, String friendName){
		boolean added = false;
		RemoveFriendRequest friendReq = new RemoveFriendRequest();
		friendReq.setSessionId(sessionId);
		friendReq.setFriendName(friendName);
		
		Query query = new Query();
		query.setQueryId(QueryId.RemoveFriend.toString());
		query.setRemoveFriendRequest(friendReq);
		
		try {
			outputStream.writeUTF(mapper.writeValueAsString(query));
			outputStream.flush();

			String resStr = inputStream.readUTF();
			Result result = mapper.readValue(resStr, Result.class);
			added = result.isSuccess();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return added;
	}
	
	public List<Friend> getFriends(String sessionId){
		List<Friend> friends = new ArrayList<Friend>();
		GetFriendsRequest getFriendsReq = new GetFriendsRequest();
		getFriendsReq.setSessionId(sessionId);
		
		Query query = new Query();
		query.setQueryId(QueryId.GetFriends.toString());
		query.setGetFriendsRequest(getFriendsReq);
		
		try {
			outputStream.writeUTF(mapper.writeValueAsString(query));
			outputStream.flush();

			String resStr = inputStream.readUTF();
			Result result = mapper.readValue(resStr, Result.class);
			friends.addAll(result.getGetFriendsResult().getFriends());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return friends;
	}

	public void createGroup(String userName) {
		AddGroupRequest grpRequest = new AddGroupRequest();
		grpRequest.setUserName(userName);

		Query query = new Query();
		query.setQueryId(QueryId.CreateGroup.toString());
		query.setAddGroupRequest(grpRequest);
		try {
			outputStream.writeUTF(mapper.writeValueAsString(query));
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addUserToGroup(String userName, String groupId) {
		AddGroupRequest grpRequest = new AddGroupRequest();
		grpRequest.setUserName(userName);
		grpRequest.setGroupId(groupId);

		Query query = new Query();
		query.setQueryId(QueryId.AddToGroup.toString());
		query.setAddGroupRequest(grpRequest);
		try {
			outputStream.writeUTF(mapper.writeValueAsString(query));
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		/*
		 * Thread auth = new Authenticator(args[0], args[1]); auth.start();
		 */
		Client client = new Client();
		//System.out.println(createUser("abby", "123"));
		String sessionId = client.authenticate("ryan", "123");
		System.out.println(client.addFriend(sessionId, "abby"));
		List<Friend> friends = client.getFriends(sessionId);
		for(Friend f : friends){
			System.out.println(f.getUserName() + " "+ f.getIp() + " " + f.getPort());
		}
		
		
		Thread.sleep(5000);
	}

}
