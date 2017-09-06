package com.ortega.scribble.gui;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

import com.ortega.scribble.context.GraphicContext;
import com.ortega.scribble.data.Message;
import com.ortega.scribble.data.impl.PenDown;
import com.ortega.scribble.data.impl.PenMove;
import com.ortega.scribble.io.ScribbleProcessor;

@SuppressWarnings("serial")
public class ScribblePanel extends JPanel
		implements MouseMotionListener, MouseListener {
		
	private boolean painting, erasing;
	private GraphicContext graphicContext;

	private ScribbleProcessor processor;
	
	public ScribblePanel(GraphicContext graphicContext, ScribbleProcessor processor) {
		this.graphicContext = graphicContext;
		this.processor = processor;
		BufferedImage canvas = graphicContext.getCanvas();
		this.setSize(canvas.getWidth(), canvas.getHeight());
		painting = false;
		addMouseListener(this);
		addMouseMotionListener(this);
		
		this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(graphicContext.getCanvas(), 0, 0, null);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (painting) {
			Message msg = new PenMove((short) e.getX(), (short) e.getY(), erasing);
			processor.send(msg);
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
			processor.send(msg);
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
			
}