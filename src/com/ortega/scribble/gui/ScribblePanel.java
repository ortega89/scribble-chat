package com.ortega.scribble.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ortega.scribble.Constants;
import com.ortega.scribble.ScribbleProcessor;
import com.ortega.scribble.context.GraphicContext;
import com.ortega.scribble.context.SwingUsersContext;
import com.ortega.scribble.context.UsersContext;
import com.ortega.scribble.data.DrawAction;
import com.ortega.scribble.data.Message;
import com.ortega.scribble.data.UserAction;
import com.ortega.scribble.data.impl.LoginResponse;
import com.ortega.scribble.data.impl.PenDown;
import com.ortega.scribble.data.impl.PenMove;

@SuppressWarnings("serial")
public class ScribblePanel extends JPanel
		implements MouseMotionListener, MouseListener, ActionListener {
	
	private static final Logger logger = LoggerFactory.getLogger(ScribblePanel.class);
	
	private BufferedImage canvas;
	
	private boolean painting, erasing;
	private GraphicContext graphicContext;
	private UsersContext usersContext;

	//private LoginResponse loginData;
	private ScribbleProcessor proc;
	private Timer timer;
	private UsersPanel usersList;
	private JFrame parent;
	
	public ScribblePanel(JFrame parent, LoginResponse loginData, ScribbleProcessor proc, UsersPanel usersList) {
		this.parent = parent;
		this.proc = proc;
		this.usersList = usersList;
		
		canvas = new BufferedImage(loginData.getWidth(), loginData.getHeight(), BufferedImage.TYPE_INT_RGB);
		graphicContext = new GraphicContext(canvas);
		usersContext = new SwingUsersContext(loginData.getUserIndex(), usersList);
		
		this.setSize(loginData.getWidth(), loginData.getHeight());
		canvas.getGraphics().setColor(Color.WHITE);
		canvas.getGraphics().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		painting = false;
		addMouseListener(this);
		addMouseMotionListener(this);
		
		timer = new Timer(10, this);
		timer.start();
		
		this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
	}
	
	public UsersPanel getUsersList() {
		return usersList;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(canvas, 0, 0, null);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (painting) {
			Message msg = new PenMove((short) e.getX(), (short) e.getY(), erasing);
			proc.send(msg);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3) {
			painting = true;
			erasing = e.getButton() == MouseEvent.BUTTON3;
			if (erasing)
				this.setCursor(new Cursor(Cursor.HAND_CURSOR));
			Message msg = new PenDown((short) e.getX(), (short) e.getY(), erasing);
			proc.send(msg);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (erasing)
			this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		painting = false;
	}

	@Override
	public void mouseMoved(MouseEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	/**
	 * A timer action handler that reads incoming messages.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		long lastHeartBeat = usersContext.getLastHeartbeatTime();
		long now = System.currentTimeMillis();
		if (lastHeartBeat != 0) {
			long timeSinceHeartBeat = now - lastHeartBeat;
			if (timeSinceHeartBeat > Constants.HEARTBEAT_TIMEOUT) {
				timer.stop();
				JOptionPane.showMessageDialog(parent, "Connection lost");
				parent.dispose();
				return;
			}
		}
			
		while (proc.canRead()) {
			Message msg;
			try {
				msg = proc.read();
				logger.debug("Received message: {}", msg);
				if (msg != null) {
					if (msg instanceof DrawAction) {
						((DrawAction) msg).doDrawAction(graphicContext);
						repaint();
					}
					if (msg instanceof UserAction)
						((UserAction) msg).doUserAction(usersContext);
					usersContext.updateHeartbeatTime();
				}
			} catch (IOException io) {
				logger.error("", io);
			}

		}
	}
			
}