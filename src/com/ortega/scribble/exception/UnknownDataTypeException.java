package com.ortega.scribble.exception;

import java.io.IOException;

@SuppressWarnings("serial")
public class UnknownDataTypeException extends IOException {
	public UnknownDataTypeException(String message) {
		super(message);
	}
}
