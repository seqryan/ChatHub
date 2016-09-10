package com.auth.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.codehaus.jackson.map.ObjectMapper;

import com.auth.beans.AddFriendRequest;
import com.auth.beans.AddGroupRequest;
import com.auth.beans.AuthenticateUserRequest;
import com.auth.beans.AuthenticateUserResult;
import com.auth.beans.CreateGroupResult;
import com.auth.beans.CreateUserRequest;
import com.auth.beans.Friend;
import com.auth.beans.GetFriendsRequest;
import com.auth.beans.GetFriendsResult;
import com.auth.beans.IdentifyIPRequest;
import com.auth.beans.IdentifyIPResult;
import com.auth.beans.Query;
import com.auth.beans.Query.QueryId;
import com.auth.beans.RemoveFriendRequest;
import com.auth.beans.Result;

public class AuthRequestHandler extends Thread {
	Socket socket;
	Connection conn;
	ObjectMapper mapper = new ObjectMapper();
	Authenticator auth;
	static Map<String, GroupManager> groups = Collections.synchronizedMap(new HashMap<String, GroupManager>());

	public AuthRequestHandler(Socket socket, Connection conn, Authenticator auth) {
		this.socket = socket;
		this.conn = conn;
		this.auth = auth;
	}

	@Override
	public void run() {
		try {
			DataInputStream inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

			while (true) {
				// get an input
				String requestStr = inputStream.readUTF();
				Query query = mapper.readValue(requestStr, Query.class);
				QueryId queryId = QueryId.fromString(query.getQueryId());
				Result result = new Result();
				String resultStr = null;
				String sessionId = null;
				String userName = null;
				String groupId = null;
				AddGroupRequest addGroupRequest;

				switch (queryId) {
				case CreateUser:
					CreateUserRequest request = query.getCreateUserRequest();
					System.out.println("Creating user with username " + request.getUserName() + " and password "
							+ request.getPassword());

					try {
						if (createUser(request.getUserName(), request.getPassword())) {
							result.setSuccess(true);
						} else {
							result.setSuccess(false);
							result.setErrorMessage("Unable to create user");
						}
					} catch (UserExistsException e) {
						result.setSuccess(false);
						result.setErrorMessage("User exists");
					}

					resultStr = mapper.writeValueAsString(result);
					outputStream.writeUTF(resultStr);
					outputStream.flush();
					break;

				case Authenticate:
					AuthenticateUserRequest authReq = query.getAuthUserRequest();
					userName = authReq.getUserName();
					String password = authReq.getPassword();
					String ip = authReq.getIp();
					String port = authReq.getPort();
					String groupport = authReq.getGroupport();

					sessionId = authenticate(userName, password, ip, port, groupport);
					if (null != sessionId) {
						result.setSuccess(true);
						AuthenticateUserResult authRes = new AuthenticateUserResult();
						authRes.setSessionId(sessionId);
						result.setAuthUserResult(authRes);
					} else {
						result.setSuccess(false);
						result.setErrorMessage("Unable to generate Session ID");
					}

					resultStr = mapper.writeValueAsString(result);
					outputStream.writeUTF(resultStr);
					outputStream.flush();
					break;

				case AddFriend:
					AddFriendRequest friendReq = query.getFriendRequest();
					sessionId = friendReq.getSessionId();
					String friendName = friendReq.getFriendName();
					if (addFriend(sessionId, friendName)) {
						result.setSuccess(true);
					} else {
						result.setSuccess(false);
						result.setErrorMessage("Unable to add friend");
					}
					resultStr = mapper.writeValueAsString(result);
					outputStream.writeUTF(resultStr);
					outputStream.flush();
					break;

				case RemoveFriend:
					RemoveFriendRequest delFriendReq = query.getRemoveFriendRequest();
					sessionId = delFriendReq.getSessionId();
					String delFriendName = delFriendReq.getFriendName();
					if (removeFriend(sessionId, delFriendName)) {
						result.setSuccess(true);
					} else {
						result.setSuccess(false);
						result.setErrorMessage("Unable to delete friend");
					}
					resultStr = mapper.writeValueAsString(result);
					outputStream.writeUTF(resultStr);
					outputStream.flush();
					break;

				case GetFriends:
					GetFriendsRequest getFriendsReq = query.getGetFriendsRequest();
					sessionId = getFriendsReq.getSessionId();

					List<Friend> friends = getFriends(sessionId);
					GetFriendsResult getFriendsRes = new GetFriendsResult();
					getFriendsRes.setFriends(friends);
					result.setGetFriendsResult(getFriendsRes);
					result.setSuccess(true);

					resultStr = mapper.writeValueAsString(result);
					outputStream.writeUTF(resultStr);
					outputStream.flush();
					break;

				case IdentifyIP:
					IdentifyIPRequest identifyRequest = query.getIdentifyIPRequest();
					String ipStr = identifyRequest.getIp();
					result.setSuccess(false);
					String identifiedName = identifyUser(ipStr);
					if (null != identifiedName) {
						IdentifyIPResult identifyResult = new IdentifyIPResult();
						identifyResult.setUserName(identifiedName);
						result.setSuccess(true);
						result.setIdentifyIPResult(identifyResult);
					}
					resultStr = mapper.writeValueAsString(result);
					outputStream.writeUTF(resultStr);
					outputStream.flush();
					break;

				case CreateGroup:
					addGroupRequest = query.getAddGroupRequest();
					userName = addGroupRequest.getUserName();
					groupId = createGroup(userName);
					//result.setSuccess(false);
					// add user to group
					if (null != groupId) {
						//result.setSuccess(false);
						addUsertoGroup(userName, groupId);
						//CreateGroupResult grpResult = new CreateGroupResult();
						//grpResult.setGroupId(groupId);
						//result.setCreateGroupResult(grpResult);
					}
					//resultStr = mapper.writeValueAsString(result);
					//outputStream.writeUTF(resultStr);
					//outputStream.flush();
					break;
					
				case AddToGroup:
					addGroupRequest = query.getAddGroupRequest();

					if (null != addGroupRequest) {
						userName = addGroupRequest.getUserName();
						groupId = addGroupRequest.getGroupId();
						System.out.println("Add user request " + userName + " " + groupId);
						if (null != userName && null != groupId) {
							addUsertoGroup(userName, groupId);
						}
					}
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String authenticate(String userName, String password, String ip, String port, String groupPort) {
		String sessionId = null;
		try {
			PreparedStatement ps = conn
					.prepareStatement("select count(*) as count from users where username = ? and password = ?");
			ps.setString(1, userName);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			rs.next();
			if (rs.getInt("count") == 1) {
				// delete old token
				ps = conn.prepareStatement("delete from session where username = ?");
				ps.setString(1, userName);
				ps.executeUpdate();

				// generate new token
				ps = conn.prepareStatement("insert into session values(?, uuid(), ?, ?, ?);");
				ps.setString(1, userName);
				ps.setString(2, ip);
				ps.setString(3, port);
				ps.setString(4, groupPort);
				ps.executeUpdate();

				ps = conn.prepareStatement("select sessionid from session where username = ?");
				ps.setString(1, userName);
				rs = ps.executeQuery();
				rs.next();
				sessionId = rs.getString(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return sessionId;
	}

	public boolean createUser(String userName, String password) throws UserExistsException {
		try {
			// check if user exists
			PreparedStatement ps = conn
					.prepareStatement("select count(*) as count from users where username = ? and password = ?");
			ps.setString(1, userName);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			rs.next();
			if (rs.getInt("count") == 1) {
				throw new UserExistsException();
			}

			ps = conn.prepareStatement("insert into users values(?, ?);");
			ps.setString(1, userName);
			ps.setString(2, password);

			return (ps.executeUpdate() == 1) ? true : false;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean addFriend(String sessionId, String friend) {
		try {
			PreparedStatement ps = conn.prepareStatement("insert into friends values(?, ?);");
			ps.setString(1, getUserName(sessionId));
			ps.setString(2, friend);
			return (ps.executeUpdate() == 1) ? true : false;
		} catch (SQLIntegrityConstraintViolationException e) {
			// user does exist
			System.out.println("Friend does not exist");
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean removeFriend(String sessionId, String friend) {
		try {
			PreparedStatement ps = conn.prepareStatement("delete from friends where username = ? and friend = ?;");
			ps.setString(1, getUserName(sessionId));
			ps.setString(2, friend);
			return (ps.executeUpdate() == 1) ? true : false;
		} catch (SQLIntegrityConstraintViolationException e) {
			// user does exist
			System.out.println("Friend does not exist");
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public List<Friend> getFriends(String sessionId) {
		List<Friend> friends = new ArrayList<Friend>();
		PreparedStatement ps;
		try {
			String userName = getUserName(sessionId);
			// receives a request ever 5 seconds. consider it as a heart beat
			auth.updateSessionStatus(userName);
			ps = conn.prepareStatement(
					"select friends.friend , session.ip, session.port from session right join friends on session.username = friends.friend where friends.username = ?");
			ps.setString(1, userName);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String friend = rs.getString(1);
				String ip = rs.getString(2);
				String portStr = rs.getString(3);
				int port = (null == portStr) ? 0 : Integer.parseInt(portStr);
				friends.add(new Friend(friend, ip, port));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return friends;
	}

	public String identifyUser(String ipAddr) {
		String userName = null;
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement("select username from session where ip = ?");
			ps.setString(1, ipAddr);
			ResultSet rs = ps.executeQuery();
			rs.next();
			userName = rs.getString(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userName;
	}

	private String getUserName(String sessionId) {
		String userName = null;
		try {
			PreparedStatement ps = conn.prepareStatement("select username  from session where sessionid = ?;");
			ps.setString(1, sessionId);
			ResultSet rs = ps.executeQuery();
			rs.next();
			userName = rs.getString(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userName;
	}

	private Friend getUserDetails(String userName) {
		Friend user = null;
		try {
			PreparedStatement ps = conn.prepareStatement("select ip, groupport  from session where username = ?;");
			ps.setString(1, userName);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String ip = rs.getString(1);
				int port = Integer.parseInt(rs.getString(2));
				user = new Friend(userName, ip, port);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	private String createGroup(String userName) {
		String groupId = UUID.randomUUID().toString();
		GroupManager mgr = new GroupManager(groupId);
		groups.put(groupId, mgr);
		System.out.println("Creating group for user " + userName);
		return groupId;
	}

	private void addUsertoGroup(String userName, String groupId) {
		GroupManager grpMgr = groups.get(groupId);
		if (null != grpMgr ) {
			Friend f = getUserDetails(userName);
			if (null != f) {
				System.out.println("Adding user " + userName + " to group " + groupId);
				grpMgr.addToGroup(userName, f.getIp(), f.getPort());
			}
		}
	}

	public static void main(String[] args) throws UnknownHostException, SocketException {
		// AuthRequestHandler h = new AuthRequestHandler(null, null, null);
		// System.out.println(h.removeFriend("fca96619-7284-11e6-b3a5-00acdfb87243",
		// "brian"));
		// System.out.println(h.removeFriend("fca96619-7284-11e6-b3a5-00acdfb87243",
		// "brian"));

		System.out.println("Your Host addr: " + InetAddress.getLocalHost().getHostAddress()); // often
																								// returns
																								// "127.0.0.1"
	}

}
