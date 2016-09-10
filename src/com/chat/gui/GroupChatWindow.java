package com.chat.gui;

import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import com.auth.util.Client;
import com.chat.gui.ChatWindow.Alias;
import com.chat.util.GroupChatReader;
import com.chat.util.SocketDisconnectException;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GroupChatWindow extends JFrame {

	private JPanel contentPane;
	private Socket appSocket;
	private JTextField addUserTextField;
	private Client client;
	private JTextArea input;
	private JButton send;
	private JTextArea display;
	private String groupId;
	private DataOutputStream dout;
	private String userName;

	private static final int ENTER_KEY_CODE = 10;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GroupChatWindow frame = new GroupChatWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public GroupChatWindow(String userName, Socket socket, Client client, String groupId) {
		this();
		this.appSocket = socket;
		this.client = client;
		this.groupId = groupId;
		this.userName = userName;
		try {
			dout = new DataOutputStream(new BufferedOutputStream(appSocket.getOutputStream()));
		} catch (IOException e) {
			handleDisconnect();
		}
		Thread inputThread = new Thread(new GroupChatReader(socket, this));
		inputThread.start();
		this.setEnabled(true);
		this.setVisible(true);
	}

	public GroupChatWindow() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent arg0) {
				System.out.println("Closing group chat window");
				try {
					if (null != appSocket) {
						appSocket.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 622, 594);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		addUserTextField = new JTextField();
		addUserTextField.setBounds(22, 13, 202, 22);
		contentPane.add(addUserTextField);
		addUserTextField.setColumns(10);

		JButton btnAddUser = new JButton("Add User");

		btnAddUser.setBounds(239, 12, 97, 25);
		contentPane.add(btnAddUser);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(22, 48, 558, 369);
		contentPane.add(scrollPane);

		display = new JTextArea();
		display.setEditable(false);
		display.setLineWrap(true);
		scrollPane.setViewportView(display);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(22, 430, 448, 89);
		contentPane.add(scrollPane_1);

		input = new JTextArea();
		scrollPane_1.setViewportView(input);

		send = new JButton("Send");

		send.setBounds(483, 430, 97, 89);
		contentPane.add(send);

		send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (null != dout && !isEmpty(input.getText().trim())) {
					try {
						dout.writeUTF(userName + " : " +input.getText().trim());
						dout.flush();
						input.setText("");
					} catch (IOException e1) {
						// connection failed
						handleDisconnect();
					}
				}
			}
		});

		input.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == ENTER_KEY_CODE) {
					if (null != dout && !isEmpty(input.getText().trim())) {
						try {
							dout.writeUTF(userName + " : " +input.getText().trim());
							dout.flush();
							input.setText("");
						} catch (IOException e1) {
							handleDisconnect();
						}
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == ENTER_KEY_CODE) {
					input.setText("");
				}
			}
		});

		btnAddUser.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String friendName = addUserTextField.getText();
				addUserTextField.setText("");
				if (!isEmpty(friendName)) {
					client.addUserToGroup(friendName, groupId);
				}
			}
		});

	}

	private static boolean isEmpty(String str) {
		return (null != str && str.isEmpty());
	}

	public void writeToDisplay(String message) {
		display.append("\n" + message);

	}

	public void handleDisconnect() {
		input.setEditable(false);
		send.setEnabled(false);

		display.append("\n\n Connection disconnected. Reconnect to continue conversation \n");

	}
}
