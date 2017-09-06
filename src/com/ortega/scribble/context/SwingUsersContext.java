package com.ortega.scribble.context;

import java.awt.Color;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;

import com.ortega.scribble.SwingUserEntry;
import com.ortega.scribble.UserEntry;
import com.ortega.scribble.gui.CellRenderer;

public class SwingUsersContext extends UsersContext {

	private JList<UserEntry> list;
	private DefaultListModel<UserEntry> model;
	private CellRenderer renderer;
	
	public SwingUsersContext(byte myIndex, JList<UserEntry> list) {
		super(myIndex);
		this.list = list;
		if (list.getModel() == null)
			list.setModel(new DefaultListModel<>());
		this.model = (DefaultListModel<UserEntry>) list.getModel();
		if (list.getCellRenderer() == null)
			list.setCellRenderer(new CellRenderer(myIndex));
		this.renderer = (CellRenderer) list.getCellRenderer();
	}
	
	public ListModel<UserEntry> getModel() {
		return model;
	}

	@Override
	protected void doAddUser(UserEntry user) {
		if (user.getIndex() == getMyIndex())
			model.add(0, user);
		else
			model.addElement(user);
		list.revalidate();
	}

	@Override
	protected void doRemoveUser(byte userIndex) {
		model.removeElement(names.get(userIndex));
		list.revalidate();
	}
	
	@Override
	protected void doRenameUser(byte userIndex, String userName) {
		list.repaint();
	}

	@Override
	public void setCleaner(byte userIndex) {
		renderer.setLastCleaner(userIndex);
		list.revalidate();
		list.repaint();
	}

	@Override
	protected void doSetUserColor(byte userIndex, Color color) {
		list.repaint();
	}

	@Override
	protected UserEntry createUserEntry(String name, byte userIndex) {
		return new SwingUserEntry(name, userIndex);
	}
}
