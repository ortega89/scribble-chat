package com.ortega.scribble.data.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ortega.scribble.context.GreetingContext;
import com.ortega.scribble.data.Message;
import com.ortega.scribble.data.PrivateMessage;
import com.ortega.scribble.data.ServiceMessage;
import com.ortega.scribble.exception.UnknownDataTypeException;

public class LoginResponse extends PrivateMessage {

	public static final short ID = 255;
	
	private short width;
	private short height;
	private List<Message> events = new ArrayList<>();
	
	//For prototyping
	public LoginResponse() {}
	
	public LoginResponse(GreetingContext context) {
		this.setUserIndex(context.newUser());
		this.width = context.getWidth();
		this.height = context.getHeight();
		copyEvents(context.getEvents());
	}
	
	public LoginResponse(short width, short height) {
		this.width = width;
		this.height = height;
	}

	private void addEvent(Message event) {
		events.add(event);
	}
	
	private void copyEvents(List<Message> list) {
		events.clear();
		for (Message msg : list)
			if (msg != null && !(msg instanceof ServiceMessage))
				events.add(msg);
	}
	
	@Override
	public short getId() {
		return ID;
	}
	
	public short getWidth() {
		return width;
	}
	
	public short getHeight() {
		return height;
	}
	
	public List<Message> getEvents() {
		return Collections.unmodifiableList(events);
	}

	@Override
	protected List<Byte> doEncode() throws UnknownDataTypeException {
		List<Byte> bytes = encodeToList((short) width, (short) height);
		for (Message event : events)
			bytes.addAll(event.encodeSigned());
		bytes.add((byte) 0);
		return bytes;
	}

	@Override
	public Message doDecode(InputStream is) throws IOException {
		LoginResponse msg = new LoginResponse(getShort(is), getShort(is));
		while (true) {
			int nextEventUserIndex = is.read();
			if (nextEventUserIndex == 0)
				break;
			Message event = readUnsigned(is);
			if (event != null) {
				event.setUserIndex((short) nextEventUserIndex);
				msg.addEvent(event);
			}
		}
		return msg;
	}
	
	@Override
	public String toString() {
		return String.format("LoginResponse(canvas size = %dx%d, events count = %d)",
				width, height, events.size());
	}
}
