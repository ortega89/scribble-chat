package com.ortega.scribble;

import java.awt.Color;
import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ortega.scribble.data.Message;
import com.ortega.scribble.data.MessageType;

public class ScribbleProcess implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(ScribbleProcess.class);
	
	private Thread thread;
	private Socket in;
	private NavigableQueue queue;
	private Context context;
	private Color userColor;
	private Message login;
	
	public ScribbleProcess(Socket in, Context context) {
		this.in = in;
		this.context = context;
		this.queue = new NavigableQueue(context.getEvents());
		this.userColor = Color.BLACK;
		thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void run() {
		ScribbleProcessor proc = null;
		try {
			proc = ScribbleProcessor.createServerProcessor(in); 
		} catch (IOException e) {
			logger.error("Failed to create a server-side Scribble processor", e);
		}
		
		login = new Message(MessageType.LOGINRESPONSE, context.newUser());
		login.setWidth(context.getWidth());
		login.setHeight(context.getHeight());
		login.copyEvents(context.getEvents());
		if (!proc.send(login)) {
			logger.error("Login failed");
			return;
		}
			
		queue.offer(new Message(MessageType.LOGINFANOUT, login.getUserIndex()));
		
		boolean closed = false;
		
		while (!closed) {
			try {
				//First of all, try to notify our client about incoming events
				while (!queue.isEmpty()) {
					Message fan = queue.poll();
					if (fan == null)
						continue;
					boolean sent = proc.send(fan);
					if (logger.isDebugEnabled()) {
						StringBuilder s = new StringBuilder("Sending message: ");
						s.append(fan.toString());
						if (sent)
							s.append("\nSuccessfully");
						else {
							s.append("\nFailed");
						}
						logger.debug("{}", s);
					}
					if (!sent)
						closed = true;
				}
								
				//Now try to read an incoming message
				if (proc.canRead()) {
					Message msg = proc.read();
					
					if (logger.isDebugEnabled()) {
						logger.debug("Received from {}", in.getInetAddress());
						logger.debug(msg.toString());
					}

					switch (msg.getType()) {
					case PICKCOLOR:
						userColor = msg.getColor();
					case SETNAME:
						queue.offer(msg);
						break;
					case CLEAR:
						queue.clearDrawings();
						queue.offer(msg);
						break;
					case PENDOWN:
					case PENMOVE:
						msg.setColor(userColor);
						queue.offer(msg);
						break;
					}
				} else {
					Thread.sleep(20);
				}
			} catch (IOException e) {
				logger.error("", e);
			} catch (InterruptedException e) {
				logger.error("", e);
			}
		}
		logger.info("Process finished");
		queue.offer(new Message(MessageType.LOGOUTFANOUT, login.getUserIndex()));
	}
	
}
