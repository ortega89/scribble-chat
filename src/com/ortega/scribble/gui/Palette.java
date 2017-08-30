package com.ortega.scribble.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

import com.ortega.scribble.ScribbleProcessor;
import com.ortega.scribble.data.Message;
import com.ortega.scribble.data.MessageType;

@SuppressWarnings("serial")
public class Palette extends JPanel {
	
	private BufferedImage palette;
	private ScribbleProcessor proc;
	private Message loginData;
	
	public Palette(int width, int height, ScribbleProcessor proc, Message loginData) {
		this.proc = proc;
		this.loginData = loginData;
		this.setPreferredSize(new Dimension(width, height));
		palette = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		generatePalette(palette, 0.9f);
		
		this.addMouseListener(new PanelMouseListener());
		this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
	}
	
	private void generatePalette(BufferedImage img, float coloredPart) {
		for (int y = 0; y < img.getHeight(); y++) {
			float pow = 2;
			float arg = (float) y / (img.getHeight() * coloredPart);
			float brightness;
			if (arg >= 0.5)
				brightness = (float) Math.pow(1 - Math.pow(2 * arg - 1, pow), 1 / pow);
			else
				brightness = 1;

			for (int x = 0; x < img.getWidth(); x++) {
				if (arg <= 1)
					palette.setRGB(x, y, Color.HSBtoRGB(
							(float) x / img.getWidth(), 
							1 - Math.abs((float) arg - 1), 
							brightness));
				else
					palette.setRGB(x, y, Color.BLACK.getRGB());
			}
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(palette, 0, 0, null);
	}

	public class PanelMouseListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				Message msg = new Message(MessageType.PICKCOLOR, loginData.getUserIndex());
				msg.setColor(new Color(palette.getRGB(e.getX(), e.getY())));
				proc.send(msg);
			}
		}	
	}
}