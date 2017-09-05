package com.ortega.scribble.data.impl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.ortega.scribble.Constants;
import com.ortega.scribble.context.GraphicContext;
import com.ortega.scribble.data.Cleanable;
import com.ortega.scribble.data.DrawAction;
import com.ortega.scribble.data.Message;
import com.ortega.scribble.data.PublicMessage;
import com.ortega.scribble.exception.UnknownDataTypeException;

public class PenDown extends PublicMessage 
		implements Cleanable, DrawAction {

	public static final short ID = 3;
	
	private short x;
	private short y;
	private boolean erasing;
	
	public PenDown(short x, short y, boolean erasing) {
		this.x = x;
		this.y = y;
		this.erasing = erasing;
	}

	@Override
	public short getId() {
		return ID;
	}
	
	public short getX() {
		return x;
	}
	
	public short getY() {
		return y;
	}
	
	public boolean isErasing() {
		return erasing;
	}

	@Override
	public void doDrawAction(GraphicContext ctx) {
		ctx.setPenPosition(this.getUserIndex(), new Point(this.getX(), this.getY()));
		Color foreColor = ctx.getPenColor(this.getUserIndex());
		
		Graphics2D g = (Graphics2D) ctx.getCanvas().getGraphics();
		g.setStroke(this.isErasing() ? Constants.ERASER_STROKE : Constants.PEN_STROKE);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(this.isErasing() ? Constants.BACK_COLOR : foreColor);
		g.drawLine(this.getX(), this.getY(), this.getX(), this.getY());
	}

	@Override
	protected List<Byte> doEncode() throws UnknownDataTypeException {
		return encodeToList(x, y, erasing);
	}

	@Override
	public Message doDecode(InputStream is) throws IOException {
		return new PenDown(getShort(is), getShort(is), getBoolean(is));
	}
	
	@Override
	public String toString() {
		return String.format("PenDown(x: %d, y: %d, %s)", 
				x, y, erasing ? "erasing" : "drawing");
	}
}
