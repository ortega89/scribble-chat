package com.ortega.scribble.data;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Message {

	private MessageType type;
	private short userIndex;
	private String name;
	private Color color;
	private boolean erase;
	private short x, y, width, height;
	private List<Message> events = new ArrayList<Message>();
	
	public Message(MessageType type, short userIndex) {
		this.type = type;
		this.userIndex = userIndex;
	}

	public byte getUserIndex() {
		return (byte) userIndex;
	}

	public void setUserIndex(short userIndex) {
		this.userIndex = userIndex;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public short getX() {
		return x;
	}

	public void setX(short x) {
		this.x = x;
	}

	public short getY() {
		return y;
	}

	public void setY(short y) {
		this.y = y;
	}

	public void addEvent(Message msg) {
		events.add(msg);
	}
	
	public void copyEvents(List<Message> list) {
		events.clear();
		for (Message msg : list)
			if (msg != null && msg.getType() != MessageType.HEARTBEAT)
				events.add(msg);
	}

	public MessageType getType() {
		return type;
	}

	public List<Message> getEvents() {
		return events;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public short getWidth() {
		return width;
	}

	public void setWidth(short width) {
		this.width = width;
	}

	public short getHeight() {
		return height;
	}

	public void setHeight(short height) {
		this.height = height;
	}
	
	@Override
	public String toString() {
		Color col = color != null ? color : Color.black;
		StringBuilder s = new StringBuilder(String.format(
				"%s (User: %s (%d); x: %d; y: %d; color: #%02x%02x%02x; width: %d; height: %d)", 
				type, name, userIndex, x, y, col.getRed(), col.getGreen(), col.getBlue(), width, height));
		if (!events.isEmpty()) {
			s.append("\n> Events included:");
			for (Message event : events) {
				s.append("\n");
				s.append(event.toString());
			}
		}
		return s.toString();
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setErase(boolean erase) {
		this.erase = erase;
	}

	public boolean isErase() {
		return erase;
	}
}
