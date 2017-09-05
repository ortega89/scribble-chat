package com.ortega.scribble.data.impl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import com.ortega.scribble.context.GraphicContext;
import com.ortega.scribble.context.UsersContext;
import com.ortega.scribble.data.Cleanable;
import com.ortega.scribble.data.DrawAction;
import com.ortega.scribble.data.Message;
import com.ortega.scribble.data.PublicMessage;
import com.ortega.scribble.data.UserAction;

public class Clear extends PublicMessage 
		implements Cleanable, DrawAction, UserAction {

	public static final short ID = 5;
	
	@Override
	public short getId() {
		return ID;
	}

	@Override
	public void doDrawAction(GraphicContext ctx) {
		BufferedImage canvas = ctx.getCanvas();
		Graphics2D g = (Graphics2D) canvas.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}
	
	@Override
	public void doUserAction(UsersContext ctx) {
		ctx.setCleaner(this.getUserIndex());
	}

	@Override
	protected List<Byte> doEncode() {
		return Collections.emptyList();
	}

	@Override
	public Message doDecode(InputStream is) {
		return new Clear();
	}
	
	@Override
	public String toString() {
		return String.format("Clear(user = %d)", this.getUserIndex());
	}
}
