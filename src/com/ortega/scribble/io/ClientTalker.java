package com.ortega.scribble.io;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ortega.scribble.Constants;
import com.ortega.scribble.context.GraphicContext;
import com.ortega.scribble.context.UsersContext;
import com.ortega.scribble.data.DrawAction;
import com.ortega.scribble.data.Message;
import com.ortega.scribble.data.UserAction;

public abstract class ClientTalker implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(ClientTalker.class);
	
	private ScribbleProcessor processor;
	private GraphicContext graphicContext;
	private UsersContext usersContext;

	public ClientTalker(ScribbleProcessor proc, GraphicContext graphicContext,
			UsersContext usersContext) {
		this.processor = proc;
		this.graphicContext = graphicContext;
		this.usersContext = usersContext;
	}
	
	@Override
	public void run() {
		while (!Thread.interrupted()) {
			long lastHeartBeat = usersContext.getLastHeartbeatTime();
			long now = System.currentTimeMillis();
			if (lastHeartBeat != 0) {
				long timeSinceHeartBeat = now - lastHeartBeat;
				if (timeSinceHeartBeat > Constants.HEARTBEAT_TIMEOUT) {
					onHeartbeatTimeout();
					return;
				}
			}
				
			while (processor.canRead()) {
				Message msg;
				try {
					msg = processor.read();
					logger.debug("Received message: {}", msg);
					if (msg != null) {
						if (msg instanceof DrawAction) {
							((DrawAction) msg).doDrawAction(graphicContext);
							graphicContext.doRepaint();
						}
						if (msg instanceof UserAction)
							((UserAction) msg).doUserAction(usersContext);
						usersContext.updateHeartbeatTime();
					}
				} catch (IOException io) {
					logger.error("", io);
				}
	
			}
			
			try {
				Thread.sleep(Constants.CLIENT_DELAY_MS);
			} catch (InterruptedException e) {
				return;
			}
		}
	}

	public abstract void onHeartbeatTimeout();
}
