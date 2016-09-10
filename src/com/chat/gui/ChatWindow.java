package com.chat.gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;

import com.chat.util.ChatClientReader;
import com.chat.util.ChatTextWriter;
import com.chat.util.SocketDisconnectException;

public class ChatWindow {
	private boolean status = true;

	private static final int ENTER_KEY_CODE = 10;

	public enum Alias {
		You("You"), Friend("Friend");

		String alias;

		Alias(String alias) {
			this.alias = alias;
		}

		public String getAlias() {
			return alias;
		}
	};

	ChatTextWriter chatWriter = null;

	private JFrame frame;
	private JTextArea display;
	private JButton send;
	private JTextArea input;

	private Socket chatSocket;

	private JLabel lblChatBox;
	private JScrollPane scrollPane_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatWindow window = new ChatWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public boolean isFrameOpen(){
		return status;
	}

	/**
	 * Create the application.
	 */
	public ChatWindow() {
		initialize();
		this.frame.setVisible(true);
	}
	

	/**
	 * Create the application from server.
	 */
	public ChatWindow(Socket chatSocket, String friendName) {
		this();
		try {
			String host = chatSocket.getInetAddress().getHostAddress().toString();
			String port = String.valueOf(chatSocket.getPort());
			lblChatBox.setText(friendName);

			enableChat(chatSocket);
		} catch (SocketDisconnectException e) {
			handleDisconnect();
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 458, 533);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		lblChatBox = new JLabel("Enter IP and Port");

		send = new JButton("Send");

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		scrollPane_1 = new JScrollPane();
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane_1, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 327, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(send, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE))
						.addComponent(lblChatBox, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblChatBox)
					.addGap(18)
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 342, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(send, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 69, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		
				display = new JTextArea();
				display.setEditable(false);
				scrollPane_1.setViewportView(display);
				display.setLineWrap(true);
				display.setTabSize(4);
				display.setRows(4);
				display.setBorder(null);

		input = new JTextArea();
		input.setEditable(false);
		scrollPane.setViewportView(input);
		input.setAutoscrolls(false);
		input.setMaximumSize(new Dimension(4, 22));
		input.setLineWrap(true);
		input.setBorder(null);
		frame.getContentPane().setLayout(groupLayout);
		send.setEnabled(false);

		// events
		input.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == ENTER_KEY_CODE) {
					if (null != chatWriter && !isEmpty(input.getText().trim())) {
						try {
							System.out.println("You typed : " + input.getText());
							chatWriter.write(input.getText());
							writeToDisplay(Alias.You, input.getText().trim());
							input.setText("");
						} catch (SocketDisconnectException e1) {
							handleDisconnect();
						}
					}
				}
			}
		});

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				System.out.println("Closing chat");
				status = false;
				try {
					if (null != chatSocket) {
						chatSocket.close();
					}
				} catch (IOException e) {
				}
			}
		});

		input.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == ENTER_KEY_CODE) {
					input.setText("");
				}
			}
		});

		send.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (null != chatWriter) {
					try {
						System.out.println("You typed : " + input.getText());
						chatWriter.write(input.getText());
						writeToDisplay(Alias.You, input.getText());
						input.setText("");
					} catch (SocketDisconnectException e1) {
						// connection failed
						handleDisconnect();
					}
				}
			}
		});
	}

	private static boolean isEmpty(String str) {
		return (null != str && str.isEmpty());
	}

	public void writeToDisplay(Alias alias, String message) {
		display.append("\n" + alias.getAlias() + "\t:  " + message);
	}

	private void enableChat(Socket socket) {
		Thread inputThread = new Thread(new ChatClientReader(socket, this));
		inputThread.start();
		chatWriter = new ChatTextWriter(socket);
		send.setEnabled(true);
		input.setEditable(true);
		frame.repaint();

		// used to close when frame closed
		chatSocket = socket;

	}

	private void showError(String message) {
		JOptionPane.showMessageDialog(new JFrame(), message, "Dialog", JOptionPane.ERROR_MESSAGE);
	}

	public void handleDisconnect() {
		input.setEditable(false);
		send.setEnabled(false);

		display.append("\n\n Connection disconnected. Reconnect to continue conversation \n");
	}
}
