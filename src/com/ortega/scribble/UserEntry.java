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

public class UserEntry {

	private static final int indicatorSize = 16;

	private String name;
	private byte index;
	private ImageIcon icon;
	private static BufferedImage src;
	

	static {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gs = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gs.getDefaultConfiguration();
		
		src = gc.createCompatibleImage(indicatorSize, indicatorSize, Transparency.BITMASK);
		Graphics g = src.getGraphics();
		g.setColor(Color.BLACK);
		g.fillOval(0, 0, indicatorSize-1, indicatorSize-1);
	}
	
	private static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	public UserEntry(String name, byte index) {
		this.name = name;
		this.index = index;
		this.icon = new ImageIcon(deepCopy(src));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Icon getIcon() {
		return icon;
	}

	public void setColor(Color color) {
		BufferedImage img = (BufferedImage) icon.getImage();
		Graphics g = img.getGraphics();
		g.setColor(color);
		g.fillOval(1, 1, indicatorSize-3, indicatorSize-3);
	}

	public byte getIndex() {
		return index;
	}
	
}
