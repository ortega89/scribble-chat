package com.ortega.scribble.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import com.ortega.scribble.ScribbleProcessor;
import com.ortega.scribble.data.impl.Clear;

@SuppressWarnings("serial")
public class ClearButton extends JButton implements ActionListener {

	private ScribbleProcessor proc;
	
	public ClearButton(String text, ScribbleProcessor proc) {
		super(text);
		this.proc = proc;
		this.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		proc.send(new Clear());
	}
	
}
