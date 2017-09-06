package com.ortega.scribble.context;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ortega.scribble.UserEntry;

public abstract class UsersContext {

	private static final Logger logger = LoggerFactory.getLogger(UsersContext.class);
	
	protected Map<Byte, UserEntry> names = new HashMap<>();
	private byte myIndex;
	private long lastHeartbeatTime;
	
	public UsersContext(byte myIndex) {
		this.myIndex = myIndex;
	}
	
	public byte getMyIndex() {
		return myIndex;
	}
	
	public void addUser(byte userIndex) {
		logger.info("Adding user # "+userIndex);
		UserEntry user = createUserEntry("Guest", userIndex);
		names.put(userIndex, user);
		doAddUser(user);
	}
	
	public void removeUser(byte userIndex) {
		logger.info("Removing user # "+userIndex);
		UserEntry user = names.get(userIndex);
		if (user != null) {
			doRemoveUser(userIndex);
			names.remove(userIndex);
		}
		else
			logger.error("USER NOT FOUND, UNABLE TO REMOVE");
	}

	public void renameUser(byte userIndex, String userName) {
		logger.info("Renaming user # {} to {}", userIndex, userName);
		UserEntry user = names.get(userIndex);
		if (user != null) {
			user.setName(userName);
			doRenameUser(userIndex, userName);
		}
		else
			logger.error("USER NOT FOUND, UNABLE TO RENAME");
	}
	
	public void setUserColor(byte userIndex, Color color) {
		logger.info("Setting color for user # {} to {}", userIndex, color);
		UserEntry user = names.get(userIndex);
		if (user != null) {
			user.setColor(color);
			doSetUserColor(userIndex, color);
		}
		else
			logger.error("USER NOT FOUND, UNABLE TO SET COLOR");
	}

	protected abstract UserEntry createUserEntry(String name, byte userIndex);
	protected abstract void doAddUser(UserEntry user);
	protected abstract void doRemoveUser(byte userIndex);
	protected abstract void doRenameUser(byte userIndex, String userName);
	protected abstract void doSetUserColor(byte userIndex, Color color);
	public abstract void setCleaner(byte userIndex);

	public void updateHeartbeatTime() {
		this.lastHeartbeatTime = System.currentTimeMillis();
	}

	public long getLastHeartbeatTime() {
		return lastHeartbeatTime;
	}
}
