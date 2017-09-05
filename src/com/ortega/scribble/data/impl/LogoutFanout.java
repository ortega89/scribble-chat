package com.ortega.scribble.data.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import com.ortega.scribble.context.UsersContext;
import com.ortega.scribble.data.Message;
import com.ortega.scribble.data.PublicMessage;
import com.ortega.scribble.data.UserAction;

public class LogoutFanout extends PublicMessage
		implements UserAction {

	public static final short ID = 6;
	
	public LogoutFanout(byte userIndex) {
		setUserIndex(userIndex);
	}

	@Override
	public short getId() {
		return ID;
	}

	@Override
	public void doUserAction(UsersContext ctx) {
		ctx.removeUser(this.getUserIndex());
	}

	@Override
	protected List<Byte> doEncode() {
		return Collections.emptyList();
	}

	@Override
	public Message doDecode(InputStream is) throws IOException {
		return new LogoutFanout((byte) 0);
	}

	@Override
	public String toString() {
		return String.format("LogoutFanout(user = %d)", this.getUserIndex());
	}
}
