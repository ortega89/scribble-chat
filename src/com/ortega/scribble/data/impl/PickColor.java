package com.ortega.scribble.data.impl;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.ortega.scribble.context.GraphicContext;
import com.ortega.scribble.context.UsersContext;
import com.ortega.scribble.data.DrawAction;
import com.ortega.scribble.data.Message;
import com.ortega.scribble.data.PublicMessage;
import com.ortega.scribble.data.UserAction;
import com.ortega.scribble.exception.UnknownDataTypeException;

public class PickColor extends PublicMessage 
		implements DrawAction, UserAction {

	public static final short ID = 2;
	
	private Color color;
	
	public PickColor(int rgb) {
		this.color = new Color(rgb);
	}
	
	public PickColor(Color color) {
		this.color = color;
	}

	@Override
	public short getId() {
		return ID;
	}

	public int getRGB() {
		return color.getRGB();
	}

	@Override
	public void doDrawAction(GraphicContext ctx) {
		ctx.setPenColor(this.getUserIndex(), color);
	}

	@Override
	public void doUserAction(UsersContext ctx) {
		ctx.setUserColor(this.getUserIndex(), color);
	}

	@Override
	protected List<Byte> doEncode() throws UnknownDataTypeException {
		return encodeToList((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue());
	}

	@Override
	public Message doDecode(InputStream is) throws IOException {
		return new PickColor(new Color(is.read(), is.read(), is.read()));
	}
	
	@Override
	public String toString() {
		return String.format("PickColor(user: %d, color: #%2x%2x%2x)", 
				getUserIndex(), color.getRed(), color.getGreen(), color.getBlue());
	}
}
