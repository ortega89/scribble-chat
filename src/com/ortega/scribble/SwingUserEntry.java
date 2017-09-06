package com.ortega.scribble;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class SwingUserEntry extends UserEntry {

	private ImageIcon icon;
	private static BufferedImage src;
	
	static {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gs = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gs.getDefaultConfiguration();
		
		src = gc.createCompatibleImage(
				Constants.USER_COLOR_INDICATOR_SIZE, 
				Constants.USER_COLOR_INDICATOR_SIZE, 
				Transparency.BITMASK);
		Graphics g = src.getGraphics();
		g.setColor(Color.BLACK);
		int ovalSize = Constants.USER_COLOR_INDICATOR_SIZE-1;
		g.fillOval(0, 0, ovalSize, ovalSize);
	}
	
	public SwingUserEntry(String name, byte index) {
		super(name, index);
		this.icon = new ImageIcon(deepCopy(src));
	}

	private static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	@Override
	public void setColor(Color color) {
		BufferedImage img = (BufferedImage) icon.getImage();
		Graphics g = img.getGraphics();
		g.setColor(color);
		int ovalSize = Constants.USER_COLOR_INDICATOR_SIZE - 3;
		g.fillOval(1, 1, ovalSize, ovalSize);
	}


	public Icon getIcon() {
		return icon;
	}
}
