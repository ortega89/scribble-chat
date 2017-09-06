package com.ortega.scribble.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ortega.scribble.data.Message;
import com.ortega.scribble.exception.UnknownDataTypeException;

public class ScribbleProcessor {

	private static final Logger logger = LoggerFactory.getLogger(ScribbleProcessor.class);

	private InputStream in;
	private OutputStream out;
	private boolean isServer;
	
	private ScribbleProcessor(boolean isServer) {}
	
	private ScribbleProcessor(InputStream in, OutputStream out, boolean isServer) {
		this.in = in;
		this.out = out;
		this.isServer = isServer;
	}
	
	private ScribbleProcessor(Socket socket, boolean isServer) throws IOException {
		this(socket.getInputStream(), socket.getOutputStream(), isServer);
	}
	
	public static ScribbleProcessor createClientProcessor(Socket socket) throws IOException {
		return new ScribbleProcessor(socket, false);
	}
	
	public static ScribbleProcessor createServerProcessor(Socket socket) throws IOException {
		return new ScribbleProcessor(socket, true);
	}
	
	public Message read() throws IOException {
		return isServer ? Message.readUnsigned(in) : Message.readSigned(in);
	}
	
	private byte[] encode(Message msg) {
		try {
			List<Byte> e = isServer ? msg.encodeSigned() : msg.encodeUnsigned();
			logger.debug("Sending message: {}", e);
			byte[] q = new byte[e.size()];
			for (int i = 0; i < q.length; i++)
				q[i] = e.get(i);
			return q;
		} catch (UnknownDataTypeException e) {
			logger.error("Failed to encode "+msg, e);
			throw new RuntimeException(e);
		}
	}
	
	public boolean send(Message msg) {
		try {
			out.write(encode(msg));
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public InputStream getInputStream() {
		return in;
	}

	public void setInputStream(InputStream in) {
		this.in = in;
	}

	public OutputStream getOutputStream() {
		return out;
	}

	public void setOutputStream(OutputStream out) {
		this.out = out;
	}

	public boolean canRead() {
		try {
			return in != null && in.available() > 0;
		} catch (IOException e) {
			return false;
		}
	}
	
	public void close() {
		try {
			in.close();
			out.close();
		} catch (IOException e) {
			logger.error("Failed to close the socket streams", e);
		}
	}
}
