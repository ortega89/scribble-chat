package com.ortega.scribble.data;

public abstract class PrivateMessage extends Message {
	@Override
	public final boolean isPersonal() {
		return true;
	}
}
