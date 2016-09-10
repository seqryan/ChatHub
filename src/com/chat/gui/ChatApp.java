package com.chat.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import com.auth.beans.Friend;
import com.auth.util.Client;
import com.auth.util.FriendListUpdater;

public class ChatApp {
	Socket authSocket;
	String sessionId;
	Map<String, Friend> friendMap = new HashMap<String, Friend>();
	Map<String, ChatWindow> conversationMap = new HashMap<String, ChatWindow>();
	FriendListUpdater listUpdater;
	String appUserName;

	private JFrame frame;
	private JList friends;
	private DefaultListModel<Friend> listModel;
	private JTextField userName;
	private JPasswordField password;
	private JTextField addOrRemove;
	private JButton addFriend;
	private JButton removeFriend;
	private JButton login;

	private Client client;
	private JButton groupChat;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatApp window = new ChatApp();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ChatApp() {

		listModel = new DefaultListModel<Friend>();

		initialize();
		disableApp();

		client = new Client();
		// updateFriendList(friendList);
		frame.setVisible(true);
		listUpdater = new FriendListUpdater(this);

	}

	private String authenticate() {
		String userNameStr = userName.getText();
		String passwd = password.getText();
		appUserName = userNameStr;

		String sessionId = client.authenticate(userNameStr, passwd);
		return sessionId;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 450, 618);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		userName = new JTextField();
		userName.setToolTipText("username");
		userName.setBounds(12, 13, 146, 22);
		frame.getContentPane().add(userName);
		userName.setColumns(10);

		password = new JPasswordField();
		password.setToolTipText("password");
		password.setBounds(165, 13, 146, 22);
		frame.getContentPane().add(password);

		login = new JButton("Login");

		login.setToolTipText("login");
		login.setBounds(323, 12, 97, 25);
		frame.getContentPane().add(login);

		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.setBounds(12, 137, 408, 433);
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		addOrRemove = new JTextField();
		addOrRemove.setBounds(12, 43, 171, 22);
		panel.add(addOrRemove);
		addOrRemove.setColumns(10);

		addFriend = new JButton("Add");

		addFriend.setBounds(226, 42, 79, 25);
		panel.add(addFriend);

		removeFriend = new JButton("Remove");
		removeFriend.setBounds(317, 42, 79, 25);
		panel.add(removeFriend);

		friends = new JList(listModel);

		friends.setBounds(12, 136, 384, 279);
		panel.add(friends);
		friends.setCellRenderer(new FriendListRenderer());
		friends.setBorder(new LineBorder(new Color(0, 0, 0)));

		JLabel lblFriendList = new JLabel("Friend List");
		lblFriendList.setBounds(12, 107, 93, 16);
		panel.add(lblFriendList);

		JLabel lblAddOrRemove = new JLabel("Add or remove friends");
		lblAddOrRemove.setBounds(12, 13, 171, 16);
		panel.add(lblAddOrRemove);

		JSeparator separator = new JSeparator();
		separator.setBounds(0, 69, 432, 2);
		frame.getContentPane().add(separator);

		JLabel lblCreateAccount = new JLabel("Create Account");

		lblCreateAccount.setForeground(Color.BLUE);
		lblCreateAccount.setBounds(22, 48, 112, 16);
		frame.getContentPane().add(lblCreateAccount);
		
		groupChat = new JButton("Start Group Chat");

		groupChat.setBounds(12, 84, 408, 40);
		frame.getContentPane().add(groupChat);

		// events
		login.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (login.isEnabled()) {
					sessionId = authenticate();
					if (null == sessionId) {
						showError("Invalid username or password");
						return;
					}
					// start chat and group chat server threads
					client.startServerThreads(appUserName);
					
					// update friend list
					updateFriendList();
					enableApp();

					// start friendlist updater thread
					listUpdater.start();

				}
			}
		});
		
		groupChat.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				client.createGroup(appUserName);
			}
		});

		removeFriend.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String friendName = addOrRemove.getText();
				if (!isEmpty(friendName)) {
					if (!client.removeFriend(sessionId, friendName)) {
						showError("Couldn't remove friend " + friendName + ". Check if friend exists");
						return;
					}
				} else {
					showError("Enter friends name");
					return;
				}
				updateFriendList();
				addOrRemove.setText("");
			}
		});

		addFriend.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String friendName = addOrRemove.getText();
				if (!isEmpty(friendName)) {
					if (!client.addFriend(sessionId, friendName)) {
						showError("Couldn't add friend " + friendName + ". Check if friend exists");
						return;
					}
				} else {
					showError("Enter friends name");
					return;
				}
				updateFriendList();
				addOrRemove.setText("");
			}
		});

		friends.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Friend selected = (Friend) friends.getSelectedValue();
				if (null != selected.getIp() && 0 != selected.getPort()) {
					try {
						ChatWindow box = conversationMap.get(selected.getUserName());

						if (null != box && box.isFrameOpen()) {
							return;
						}

						System.out.println("Connecting socket");
						Socket socket = new Socket(selected.getIp(), selected.getPort());
						box = new ChatWindow(socket, selected.getUserName());
						conversationMap.put(selected.getUserName(), box);
						/*
						 * EventQueue.invokeLater(new Runnable() { public void
						 * run() { try { ChatWindow box = new ChatWindow(socket,
						 * selected.getUserName()); } catch (Exception e) {
						 * e.printStackTrace(); } } });
						 */
					} catch (UnknownHostException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		lblCreateAccount.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				CreateAccount createAccountBox = new CreateAccount(client);
				createAccountBox.setVisible(true);
			}
		});

	}

	private void showError(String message) {
		JOptionPane.showMessageDialog(new JFrame(), message, "Dialog", JOptionPane.ERROR_MESSAGE);
	}

	public void updateFriendList() {
		List<Friend> friendList = client.getFriends(sessionId);
		DefaultListModel listModel = (DefaultListModel) friends.getModel();
		listModel.removeAllElements();
		friends.removeAll();
		friendMap.clear();
		for (Friend f : friendList) {
			listModel.addElement(f);
			friendMap.put(f.getUserName(), f);
		}
	}

	private void disableApp() {
		userName.setEnabled(true);
		password.setEnabled(true);
		login.setEnabled(true);

		addOrRemove.setEnabled(false);
		addFriend.setEnabled(false);
		removeFriend.setEnabled(false);
		friends.setEnabled(false);
		groupChat.setEnabled(false);
		
	}

	private void enableApp() {
		userName.setEnabled(false);
		password.setEnabled(false);
		login.setEnabled(false);

		addOrRemove.setEnabled(true);
		addFriend.setEnabled(true);
		removeFriend.setEnabled(true);
		friends.setEnabled(true);
		groupChat.setEnabled(true);
	}

	private boolean isEmpty(String str) {
		return (null == str || str.isEmpty());
	}
}
