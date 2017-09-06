package com.ortega.scribble;

import java.awt.Color;

public abstract class UserEntry {

	private String name;
	private byte index;

	public UserEntry(String name, byte index) {
		this.name = name;
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public byte getIndex() {
		return index;
	}

	public abstract void setColor(Color color);
}
