package com.ortega.scribble;

import java.util.concurrent.CopyOnWriteArrayList;

import com.ortega.scribble.data.Message;

public class Context {

	private short width, height;
	private CopyOnWriteArrayList<Message> events;
	private short nextUser = 1;
	
	public Context(short width, short height, CopyOnWriteArrayList<Message> events) {
		this.width = width;
		this.height = height;
		this.events = events;
	}
	
	public Context(int width, int height, CopyOnWriteArrayList<Message> events) {
		this((short) width, (short) height, events);
	}
	
	public short getWidth()           { return width; }
	public void setWidth(short width) { this.width = width; }
	
	public short getHeight()            { return height; }
	public void setHeight(short height) { this.height = height; }
	
	public CopyOnWriteArrayList<Message> getEvents()            { return events; }
	public void setEvents(CopyOnWriteArrayList<Message> events) { this.events = events; }
	
	public short newUser() {
		return nextUser++;
	}
	
}
