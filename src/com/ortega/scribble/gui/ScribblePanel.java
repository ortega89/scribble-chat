package com.ortega.scribble.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ortega.scribble.ScribbleProcessor;
import com.ortega.scribble.data.Message;
import com.ortega.scribble.data.MessageType;

@SuppressWarnings("serial")
public class ScribblePanel extends JPanel
		implements MouseMotionListener, MouseListener, ActionListener {
	
	private static final Logger logger = LoggerFactory.getLogger(ScribblePanel.class);
	
	private BufferedImage canvas;
	
	private boolean painting, erasing;
	private Point penPos;
	private Map<Byte, Point> pens;

	private Message loginData;
	private ScribbleProcessor proc;
	private Timer timer;
	private UsersPanel usersList;
	private JFrame parent;
	private long lastBeat;
	
	public ScribblePanel(JFrame parent, Message loginData, ScribbleProcessor proc, UsersPanel usersList) {
		this.parent = parent;
		this.loginData = loginData;
		this.proc = proc;
		this.usersList = usersList;
		canvas = new BufferedImage(loginData.getWidth(), loginData.getHeight(), BufferedImage.TYPE_INT_RGB);
		this.setSize(loginData.getWidth(), loginData.getHeight());
		canvas.getGraphics().setColor(Color.WHITE);
		canvas.getGraphics().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		painting = false;
		addMouseListener(this);
		addMouseMotionListener(this);
		
		pens = new HashMap<Byte, Point>();
		timer = new Timer(10, this);
		timer.start();
		
		this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		
		lastBeat = System.currentTimeMillis();
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
			penPos = new Point(e.getX(), e.getY());
			Message msg = new Message(MessageType.PENMOVE, loginData.getUserIndex());
			msg.setX((short) penPos.getX());
			msg.setY((short) penPos.getY());
			msg.setErase(erasing);
			proc.send(msg);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3) {
			penPos = new Point(e.getX(), e.getY());
			painting = true;
			erasing = e.getButton() == MouseEvent.BUTTON3;
			if (erasing)
				this.setCursor(new Cursor(Cursor.HAND_CURSOR));
			Message msg = new Message(MessageType.PENDOWN, loginData.getUserIndex());
			msg.setX((short) penPos.getX());
			msg.setY((short) penPos.getY());
			msg.setErase(erasing);
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
	@SuppressWarnings("incomplete-switch")
	@Override
	public void actionPerformed(ActionEvent e) {
		if (lastBeat != 0 && System.currentTimeMillis() - lastBeat > 5000) {
			timer.stop();
			JOptionPane.showMessageDialog(parent, "Connection lost");
			parent.dispose();
			return;
		}
			
		while (proc.canRead()) {
			Message msg;
			try {
				msg = proc.read();
				if (msg != null)
					switch (msg.getType()) {
					case PENDOWN:
						pens.put(msg.getUserIndex(), new Point(msg.getX(), msg.getY()));
						Graphics2D g = (Graphics2D) canvas.getGraphics();
						g.setStroke(new BasicStroke(msg.isErase() ? 9 : 3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
						g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						g.setColor(msg.isErase() ? Color.WHITE : msg.getColor());
						g.drawLine(msg.getX(), msg.getY(), msg.getX(), msg.getY());
						repaint();
						//System.out.println("User # "+msg.getUserIndex()+": Pen down ("+msg.getX()+", "+msg.getY()+")");
						break;
					case PENMOVE:
						Point oldPen = pens.get(msg.getUserIndex());
						pens.put(msg.getUserIndex(), new Point(msg.getX(), msg.getY()));
						g = (Graphics2D) canvas.getGraphics();
						g.setStroke(new BasicStroke(msg.isErase() ? 9 : 3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
						g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						g.setColor(msg.isErase() ? Color.WHITE : msg.getColor());
						g.drawLine((short) oldPen.getX(), (short) oldPen.getY(), msg.getX(), msg.getY());
						repaint();
						//System.out.println("User # "+msg.getUserIndex()+": Pen move ("+msg.getX()+", "+msg.getY()+")");
						break;
					case PICKCOLOR:
						if (usersList != null)
							usersList.changeUserColor(msg.getUserIndex(), msg.getColor());
						break;
					case LOGINFANOUT:
						if (usersList != null)
							usersList.addUser(msg.getUserIndex());
						break;
					case SETNAME:
						if (usersList != null)
							usersList.renameUser(msg.getUserIndex(), msg.getName());
						break;
					case LOGOUTFANOUT:
						if (usersList != null)
							usersList.removeUser(msg.getUserIndex());
						break;
					case CLEAR:
						g = (Graphics2D) canvas.getGraphics();
						g.setColor(Color.WHITE);
						g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
						usersList.setCleaner(msg.getUserIndex());
						repaint();
						//System.out.println("User # "+msg.getUserIndex()+": Clear canvas");
						break;
					case HEARTBEAT:
						lastBeat = System.currentTimeMillis();
						break;
					}
			} catch (IOException io) {
				logger.error("", io);
			}

		}
	}
			
}