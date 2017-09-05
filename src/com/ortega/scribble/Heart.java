package com.ortega.scribble;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ortega.scribble.data.impl.Heartbeat;

public class Heart implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(Heart.class);
	
	private NavigableQueue queue;
	private boolean stopped;
	
	public Heart(NavigableQueue heartbeatReceiver) {
		this.queue = heartbeatReceiver;
		stopped = false;
		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}
	
	@Override
	public void run() {
		while (!stopped) {
			try {
				queue.offer(new Heartbeat());
				Thread.sleep(Constants.HEARTBEAT_DELAY);
			} catch (InterruptedException e) {
				logger.info("Heart stopped");
			}
		}
	}
	
	public void stop() {
		stopped = true;
	}
}
