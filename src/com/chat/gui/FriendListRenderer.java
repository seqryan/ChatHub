package com.chat.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.auth.beans.Friend;

public class FriendListRenderer extends JLabel implements ListCellRenderer {
	private static final Color HIGHLIGHT_COLOR = new Color(0, 0, 128);

	  public FriendListRenderer() {
	    setOpaque(true);
	    setIconTextGap(24);
	  }

	  public Component getListCellRendererComponent(JList list, Object value,
	      int index, boolean isSelected, boolean cellHasFocus) {
	    Friend entry = (Friend) value;
	    setText(entry.getUserName());
	    if(entry.getIp() != null && entry.getPort()!= 0){
	    	setIcon(new ImageIcon("img/online.jpg"));
	    } else {
	    	setIcon(new ImageIcon("img/offline.jpg"));
	    }
	    
	    if (isSelected) {
	      setBackground(HIGHLIGHT_COLOR);
	      setForeground(Color.white);
	    } else {
	      setBackground(Color.white);
	      setForeground(Color.black);
	    }
	    return this;
	  }
}
