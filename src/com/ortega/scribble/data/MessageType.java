package com.ortega.scribble.data;

public enum MessageType {
	LOGINRESPONSE((short) 255, true),
	SETNAME((short) 254),
	HEARTBEAT((short) 253),
	LOGINFANOUT((short) 1),
	PICKCOLOR((short) 2),
	PENDOWN((short) 3),
	PENMOVE((short) 4),
	CLEAR((short) 5),
	LOGOUTFANOUT((short) 6);
	
	private boolean personal = false;
	private final short index; 
	
	private MessageType(short index) {
		this.index = index;
	}
	
	private MessageType(short index, boolean personal) {
		this(index);
		this.personal = personal;
	}
	
	public boolean isPersonal() {
		return personal;
	}
	
	public short getIndex() {
		return index;
	}

	public static MessageType byIndex(short index) {
		for (MessageType type : values())
			if (type.getIndex() == index)
				return type;
		return null;
	}
}