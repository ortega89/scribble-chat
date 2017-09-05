package com.ortega.scribble.gui;

import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import com.ortega.scribble.UserEntry;

@SuppressWarnings("serial")
public class CellRenderer extends DefaultListCellRenderer {
	
	private Byte myIndex, cleaner;
	
	public CellRenderer(byte myIndex) {
		this.myIndex = myIndex;
		cleaner = null;
	}
	
	public void setLastCleaner(byte index) {
		cleaner = index;
	}
	
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		UserEntry src = (UserEntry) value;
		JLabel out = (JLabel) super.getListCellRendererComponent(
				list, src.getName(), index, isSelected, cellHasFocus);
		out.setFont(out.getFont().deriveFont(
				(src.getIndex() == myIndex ? Font.ITALIC : Font.PLAIN) | 
				(cleaner != null && src.getIndex() == cleaner ? Font.BOLD : Font.PLAIN)));
		out.setIcon(src.getIcon());
		return out;
	}
}
