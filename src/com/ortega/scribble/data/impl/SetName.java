package com.ortega.scribble.data.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.ortega.scribble.Constants;
import com.ortega.scribble.context.UsersContext;
import com.ortega.scribble.data.Message;
import com.ortega.scribble.data.PublicMessage;
import com.ortega.scribble.data.UserAction;

public class SetName extends PublicMessage implements UserAction {

	public static final short ID = 254;
	
	private String userName;
	
	public SetName(String userName) {
		this.userName = userName;
	}

	@Override
	public short getId() {
		return ID;
	}
	
	public String getUserName() {
		return userName;
	}

	@Override
	public void doUserAction(UsersContext ctx) {
		ctx.renameUser(this.getUserIndex(), this.getUserName());
	}

	@Override
	protected List<Byte> doEncode() {
		byte[] byteArray = userName.getBytes(Charset.forName(Constants.CHARSET));
		List<Byte> bytes = new ArrayList<>();
		for (byte b : byteArray)
			bytes.add(b);
		bytes.add((byte) 0);
		return bytes;
	}

	@Override
	public Message doDecode(InputStream is) throws IOException {
		List<Byte> bytes = new ArrayList<Byte>();
		byte b;
		while ((b = (byte) is.read()) != 0)
			bytes.add(b);
		byte[] bs = new byte[bytes.size()];
		for (int i = 0; i < bs.length; i++)
			bs[i] = bytes.get(i);
		String name = new String(bs, Charset.forName(Constants.CHARSET));
		return new SetName(name);
	}
	
	@Override
	public String toString() {
		return String.format("SetName(user = %d, name = %s)",
				this.getUserIndex(), this.getUserName());
	}
}
