package com.ortega.scribble.data;

public abstract class PublicMessage extends Message {
	@Override
	public final boolean isPersonal() {
		return false;
	}
}
