package com.ortega.scribble.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.ortega.scribble.ScribbleProcessor;
import com.ortega.scribble.data.Message;

@SuppressWarnings("serial")
public class ScribbleFrame extends JFrame {
	
	private static final int PALETTE_WIDTH = 160;
	private static final int PALETTE_HEIGHT = PALETTE_WIDTH;
	
	public ScribbleFrame(Message loginData, ScribbleProcessor proc) {
		this.setTitle("Scribble");
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(loginData.getWidth()+PALETTE_WIDTH+4, loginData.getHeight()+32));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		
		ScribblePanel canvas = new ScribblePanel(this, loginData, proc, new UsersPanel(loginData.getUserIndex()));
		
		canvas.setLocation(0, 0);
		
		this.add(canvas, BorderLayout.CENTER);

		JPanel right = new JPanel(new BorderLayout());
		right.setLocation(canvas.getWidth(), 0);
		right.setPreferredSize(new Dimension(PALETTE_WIDTH, canvas.getHeight()));

		JPanel tools = new JPanel(new BorderLayout());
		right.add(tools, BorderLayout.NORTH);
		
		Palette palette = new Palette(PALETTE_WIDTH, PALETTE_HEIGHT, proc, loginData);
		tools.add(palette, BorderLayout.CENTER);
				
		ClearButton clear = new ClearButton("Clear", loginData, proc);
		
		clear.setPreferredSize(new Dimension(palette.getWidth(), 32));
		tools.add(clear, BorderLayout.SOUTH);

		UsersPanel users = canvas.getUsersList();

		JScrollPane scroller = new JScrollPane(users);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		scroller.setBackground(Color.RED);
		
		right.add(scroller, BorderLayout.CENTER);
		
		this.add(right, BorderLayout.EAST);
		this.pack();
	}
}
