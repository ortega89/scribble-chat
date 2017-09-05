package com.ortega.scribble.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ortega.scribble.data.impl.*;
import com.ortega.scribble.exception.UnknownDataTypeException;
import com.ortega.scribble.exception.UnknownMessageIdException;

public abstract class Message {

	private static final Map<Short, Message> prototypes = new HashMap<>();
	
	static {
		register(new Clear());
		register(new Heartbeat());
		register(new LoginFanout((byte) 0));
		register(new LoginResponse());
		register(new LogoutFanout((byte) 0));
		register(new PenDown((short) 0, (short) 0, false));
		register(new PenMove((short) 0, (short) 0, false));
		register(new PickColor(0));
		register(new SetName(null));
	}
	
	private static void register(Message msg) {
		prototypes.put(msg.getId(), msg);
	}
	
	private short userIndex;
	
	public byte getUserIndex() {
		return (byte) userIndex;
	}

	public void setUserIndex(short userIndex) {
		this.userIndex = userIndex;
	}
	
	protected static List<Byte> encodeToList(Object... data)
			throws UnknownDataTypeException {

		List<Byte> out = new ArrayList<Byte>();
		
		for (int i = 0; i < data.length; i++) {
			if (data[i] instanceof Short) {
				short d = (Short) data[i];
				out.add((byte)( d       & 0xff));
				out.add((byte)((d >> 8) & 0xff));
			} else if (data[i] instanceof Byte) {
				out.add((Byte) data[i]);
			} else if (data[i] instanceof Boolean) {
				out.add(((Boolean) data[i]) ? (byte) 1 : (byte) 0);
			} else 
				throw new UnknownDataTypeException("Only byte, short and boolean data types are supported");
		}
		return out;
	}
	
	public List<Byte> encodeSigned() throws UnknownDataTypeException {
		List<Byte> bytes = encodeToList(getUserIndex(), getId());
		bytes.addAll(doEncode());
		//bytes.add((byte) 0);
		return bytes;
	}
	
	public List<Byte> encodeUnsigned() throws UnknownDataTypeException {
		List<Byte> bytes = encodeToList(getId());
		bytes.addAll(doEncode());
		//bytes.add((byte) 0);
		return bytes;
	}
	
	protected static short getShort(InputStream is) throws IOException {
		return getShort(is.read(), is.read());
	}
	
	protected static short getShort(int junior, int senior) {
		return (short) (junior + (senior << 8));
	}
	
	protected boolean getBoolean(InputStream is) throws IOException {
		return is.read() == 1;
	}
		
	public static Message readSigned(InputStream is) throws IOException, UnknownMessageIdException {
		int userIndex = is.read();
		if (userIndex < 0)
			return null;
		Message msg = readUnsigned(is);
		if (msg != null)
			msg.setUserIndex((short) userIndex);
		return msg;
	}
	
	public static Message readUnsigned(InputStream is) throws UnknownMessageIdException, IOException {
		int messageId = getShort(is);
		if (messageId < 0)
			return null;
		short messageIdShort = (short) messageId;
		Message prototype = prototypes.get(messageIdShort);
		if (prototype == null)
			throw new UnknownMessageIdException(messageIdShort);
		Message msg = prototype.doDecode(is);
		return msg;
	}
	
	protected abstract List<Byte> doEncode() throws UnknownDataTypeException;
	public abstract short getId();
	public abstract boolean isPersonal();
	public abstract Message doDecode(InputStream is) throws IOException;
}
