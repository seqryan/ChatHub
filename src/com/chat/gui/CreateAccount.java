package com.chat.gui;

import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.auth.util.Client;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;

public class CreateAccount extends JFrame {

	private JPanel contentPane;
	private JTextField username;
	private Client client;
	private JPasswordField passwordField;
	private CreateAccount frame = this;

	/**
	 * Launch the application.
	 */
/*	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					CreateAccount frame = new CreateAccount();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	/**
	 * Create the frame.
	 */
	public CreateAccount(Client c) {
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 307, 220);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		username = new JTextField();
		username.setBounds(102, 39, 158, 22);
		contentPane.add(username);
		username.setColumns(10);
		
		JLabel lblUserName = new JLabel("User name");
		lblUserName.setBounds(21, 42, 69, 16);
		contentPane.add(lblUserName);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(21, 77, 56, 16);
		contentPane.add(lblPassword);
		
		JButton btnCreate = new JButton("Create");

		btnCreate.setBounds(102, 119, 97, 25);
		contentPane.add(btnCreate);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(102, 74, 158, 22);
		contentPane.add(passwordField);
		
		this.client = c;
		
		// events
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String userName = username.getText();
				String password = passwordField.getText();
				if(isEmpty(userName) && isEmpty(password)){
					showError("Username and password can not be empty");
					return;
				}
				String error = client.createUser(userName, password);
				username.setText("");
				passwordField.setText("");
				if(!isEmpty(error)){
					showError(error);
					return;
				}
				
				frame.dispose();
			}
		});
	}
	
	private boolean isEmpty(String str){
		return (null == str || str.isEmpty());
	}
	
	private void showError(String message) {
		JOptionPane.showMessageDialog(new JFrame(), message, "Dialog", JOptionPane.ERROR_MESSAGE);
	}

}
