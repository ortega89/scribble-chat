package com.ortega.scribble.data.impl;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import com.ortega.scribble.data.Message;
import com.ortega.scribble.data.PublicMessage;
import com.ortega.scribble.data.ServiceMessage;

public class Heartbeat extends PublicMessage 
		implements ServiceMessage {
	
	public static final short ID = 253;
	
	@Override
	public short getId() {
		return ID;
	}

	@Override
	protected List<Byte> doEncode() {
		return Collections.emptyList();
	}

	@Override
	public Message doDecode(InputStream is) {
		return new Heartbeat();
	}
	
	@Override
	public String toString() {
		return "Heartbeat";
	}
}
