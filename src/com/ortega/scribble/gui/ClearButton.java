package com.ortega.scribble.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import com.ortega.scribble.ScribbleProcessor;
import com.ortega.scribble.data.Message;
import com.ortega.scribble.data.MessageType;

@SuppressWarnings("serial")
public class ClearButton extends JButton implements ActionListener {

	private byte userIndex;
	private ScribbleProcessor proc;
	
	public ClearButton(String text, Message loginData, ScribbleProcessor proc) {
		super(text);
		this.userIndex = loginData.getUserIndex();
		this.proc = proc;
		this.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		proc.send(new Message(MessageType.CLEAR, userIndex));
	}
	
}
