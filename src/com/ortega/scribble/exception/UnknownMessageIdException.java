package com.ortega.scribble.exception;

import java.io.IOException;

@SuppressWarnings("serial")
public class UnknownMessageIdException extends IOException {
	public UnknownMessageIdException(short messageId) {
		super("Unknown message ID: "+messageId);
	}
}
