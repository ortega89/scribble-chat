package com.ortega.scribble;

import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ortega.scribble.context.GreetingContext;
import com.ortega.scribble.data.Message;
import com.ortega.scribble.data.impl.Clear;
import com.ortega.scribble.data.impl.LoginFanout;
import com.ortega.scribble.data.impl.LoginResponse;
import com.ortega.scribble.data.impl.LogoutFanout;

public class ScribbleProcess implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(ScribbleProcess.class);
	
	private Thread thread;
	private Socket clientSocket;
	private NavigableQueue queue;
	private GreetingContext context;
	private LoginResponse login;
	
	public ScribbleProcess(Socket in, GreetingContext context) {
		this.clientSocket = in;
		this.context = context;
		this.queue = new NavigableQueue(context.getEvents());
		thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public void run() {
		ScribbleProcessor proc = null;
		try {
			proc = ScribbleProcessor.createServerProcessor(clientSocket); 
		} catch (IOException e) {
			logger.error("Failed to create a server-side Scribble processor", e);
		}
		
		login = new LoginResponse(context);
		if (!proc.send(login)) {
			logger.error("Login failed");
			return;
		}
			
		queue.offer(new LoginFanout(login.getUserIndex()));
		
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
							s.append(" successfully");
						else {
							s.append(" failed");
						}
						logger.debug("{}", s);
					}
					if (!sent)
						closed = true;
				}
								
				//Now try to read an incoming message
				if (proc.canRead()) {
					Message msg = proc.read();
					msg.setUserIndex(login.getUserIndex());
					
					if (logger.isDebugEnabled()) {
						logger.debug("Received from {}:{}", clientSocket.getInetAddress(), clientSocket.getPort());
						logger.debug(msg.toString());
					}

					if (msg instanceof Clear)
						queue.clearDrawings();
					queue.offer(msg);
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
		queue.offer(new LogoutFanout(login.getUserIndex()));
	}
	
}
