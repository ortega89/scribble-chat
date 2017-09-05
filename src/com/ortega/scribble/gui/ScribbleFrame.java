package com.ortega.scribble.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.ortega.scribble.ScribbleProcessor;
import com.ortega.scribble.data.impl.LoginResponse;

@SuppressWarnings("serial")
public class ScribbleFrame extends JFrame {
	
	private static final int PALETTE_WIDTH = 160;
	private static final int PALETTE_HEIGHT = PALETTE_WIDTH;
	
	public ScribbleFrame(LoginResponse loginData, ScribbleProcessor proc) {
		this.setTitle("Scribble");
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(loginData.getWidth()+PALETTE_WIDTH+4, loginData.getHeight()+32));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.setPreferredSize(new Dimension(PALETTE_WIDTH, PALETTE_HEIGHT));
		this.add(rightPanel, BorderLayout.EAST);

		JPanel tools = new JPanel(new BorderLayout());
		
		Palette palette = new Palette(PALETTE_WIDTH, PALETTE_HEIGHT, proc, loginData);
		tools.add(palette, BorderLayout.CENTER);
				
		ClearButton clear = new ClearButton("Clear", proc);
		
		clear.setPreferredSize(new Dimension(palette.getWidth(), 32));
		tools.add(clear, BorderLayout.SOUTH);

		UsersPanel users = new UsersPanel(loginData.getUserIndex());
		JScrollPane scroller = new JScrollPane(users);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setBackground(Color.RED);
		
		rightPanel.add(tools, BorderLayout.NORTH);
		rightPanel.add(scroller, BorderLayout.CENTER);
		
		ScribblePanel canvas = new ScribblePanel(this, loginData, proc, users);		
		this.add(canvas, BorderLayout.CENTER);
		
		this.pack();
	}
}
