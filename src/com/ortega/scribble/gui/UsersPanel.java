package com.ortega.scribble.gui;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import com.ortega.scribble.UserEntry;

@SuppressWarnings("serial")
public class UsersPanel extends JList<UserEntry> {
	public UsersPanel(byte myIndex) {
		super(new DefaultListModel<UserEntry>());
		this.setFocusable(false);
		this.setAutoscrolls(true);
		setCellRenderer(new CellRenderer(myIndex));
	}
}