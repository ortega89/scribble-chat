package com.ortega.scribble.gui;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.JList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ortega.scribble.UserEntry;

@SuppressWarnings("serial")
public class UsersPanel extends JList<UserEntry> {
	private static final Logger logger = LoggerFactory.getLogger(UsersPanel.class);
	
	private Map<Byte, UserEntry> names;
	
	private DefaultListModel<UserEntry> model;
	
	private byte myIndex;
	
	private CellRenderer renderer;
	
	public UsersPanel(byte myIndex) {
		super(new DefaultListModel<UserEntry>());
		this.model = (DefaultListModel<UserEntry>) getModel();
		this.names = new HashMap<Byte, UserEntry>();
		this.myIndex = myIndex;
		this.setFocusable(false);
		this.setAutoscrolls(true);
		setCellRenderer(renderer = new CellRenderer(myIndex));
	}

	public void addUser(byte index) {
		logger.info("Adding user # "+index);
		UserEntry user = new UserEntry("Guest", index);
		if (index != myIndex)
			model.addElement(user);
		else
			model.add(0, user);
		names.put(index, user);
		repaint();
	}
	
	public void renameUser(byte index, String name) {
		logger.info("Renaming user # "+index);
		UserEntry user = names.get(index);
		if (user != null) {
			user.setName(name);
			repaint();
		}
		else
			logger.error("USER NOT FOUND, UNABLE TO RENAME");
	}
	
	public void removeUser(byte index) {
		logger.info("Removing user # "+index);
		UserEntry user = names.get(index);
		if (user != null) {
			model.removeElement(user);
			this.revalidate();
			names.remove(index);
			repaint();
		}
		else
			logger.error("USER NOT FOUND, UNABLE TO REMOVE");
	}
	
	public void changeUserColor(byte index, Color color) {
		logger.info("Changing color for user # "+index);
		UserEntry user = names.get(index);
		if (user != null) {
			user.setColor(color);
			repaint();
		}
		else
			logger.error("USER NOT FOUND, UNABLE TO SET COLOR IN LIST");
	}

	public boolean setSelectedUserIndex(byte userIndex) {
		UserEntry user = names.get(userIndex);
		if (user == null)
			return false;
		int index = model.indexOf(user);
		if (index < 0)
			return false;
		setSelectedIndex(index);
		return true;
	}
	
	public void setCleaner(byte index) {
		renderer.setLastCleaner(index);
		repaint();
	}
}