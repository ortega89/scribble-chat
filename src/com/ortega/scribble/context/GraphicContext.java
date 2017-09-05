package com.ortega.scribble.context;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import com.ortega.scribble.Constants;

public class GraphicContext {
	private BufferedImage canvas;
	private Map<Byte, Point> penPositions = new HashMap<>();
	private Map<Byte, Color> penColors = new HashMap<>();
	
	public GraphicContext(BufferedImage canvas) {
		this.canvas = canvas;
	}
	
	public BufferedImage getCanvas() {
		return canvas;
	}
	
	public Point getPenPosition(byte userIndex) {
		return penPositions.get(userIndex);
	}
	
	public void setPenPosition(byte userIndex, Point pen) {
		penPositions.put(userIndex, pen);
	}

	public Color getPenColor(byte userIndex) {
		Color color = penColors.get(userIndex);
		return color == null ? Constants.DEFAULT_PEN_COLOR : color;
	}

	public void setPenColor(byte userIndex, Color color) {
		penColors.put(userIndex, color);
	}
	
	
}
