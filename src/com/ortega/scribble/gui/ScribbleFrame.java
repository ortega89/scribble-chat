package com.ortega.scribble.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.ortega.scribble.context.GraphicContext;
import com.ortega.scribble.context.SwingGraphicContext;
import com.ortega.scribble.context.SwingUsersContext;
import com.ortega.scribble.context.UsersContext;
import com.ortega.scribble.data.impl.LoginResponse;
import com.ortega.scribble.io.ScribbleProcessor;

@SuppressWarnings("serial")
public class ScribbleFrame extends JFrame {
	
	private static final int PALETTE_WIDTH = 160;
	private static final int PALETTE_HEIGHT = PALETTE_WIDTH;
	
	private SwingGraphicContext graphicContext;
	private SwingUsersContext usersContext;
	
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

		UsersPanel usersList = new UsersPanel(loginData.getUserIndex());
		JScrollPane scroller = new JScrollPane(usersList);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setBackground(Color.RED);
		
		rightPanel.add(tools, BorderLayout.NORTH);
		rightPanel.add(scroller, BorderLayout.CENTER);
		
		initContexts(loginData, usersList);
		
		ScribblePanel canvas = new ScribblePanel(graphicContext, proc);		
		graphicContext.setPaintBox(canvas);
		
		this.add(canvas, BorderLayout.CENTER);
		
		this.pack();		
	}
	
	private void initContexts(LoginResponse loginData, UsersPanel usersList) {
		BufferedImage canvas = new BufferedImage(
				loginData.getWidth(), loginData.getHeight(), BufferedImage.TYPE_INT_RGB);
		graphicContext = new SwingGraphicContext(canvas);
		usersContext = new SwingUsersContext(loginData.getUserIndex(), usersList);
	}

	public GraphicContext getGraphicContext() {
		return graphicContext;
	}

	public UsersContext getUsersContext() {
		return usersContext;
	}
}
