package com.ortega.scribble;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.activation.UnsupportedDataTypeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ortega.scribble.data.Message;
import com.ortega.scribble.data.MessageType;

public class ScribbleProcessor {

	private static final Logger logger = LoggerFactory.getLogger(ScribbleProcessor.class);
	
	private static final String CHARSET = "UTF-8";
	
	//private short width, height;

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
	
	private short getShort(int junior, int senior) {
		return (short) (junior + (senior << 8));
	}
	
	public Message read() throws IOException {
		return read(null);
	}
	
	public Message read(Byte firstByte) throws IOException {
		short user = firstByte != null ? (short) firstByte : (short) in.read();
		if (user < 0)
			return null;
		short type = (short) in.read();
		if (type < 0)
			return null;
		Message msg = new Message(MessageType.byIndex(type), user);
		if (msg.getType() == null)
			return null;
		switch (msg.getType()) {
		case LOGINFANOUT:
		case LOGOUTFANOUT:
		case CLEAR:
		case HEARTBEAT:
			break;
		case LOGINRESPONSE:
			msg.setWidth(getShort(in.read(), in.read()));
			msg.setHeight(getShort(in.read(), in.read()));
			do {
				Byte b = (byte) in.read();
				if (b == 0)
					return msg;
				Message event = read(b);
				if (event != null)
					msg.addEvent(event);
			} while (true);
		case PICKCOLOR:
		case PENDOWN:
		case PENMOVE:
			if (!isServer || msg.getType() == MessageType.PICKCOLOR) {
				int r = in.read(), g = in.read(), b = in.read();
				Color color = new Color(r, g, b);
				msg.setColor(color);
				if (msg.getType() == MessageType.PICKCOLOR)
					break;
			}
			msg.setErase(in.read() == 1);
			msg.setX(getShort(in.read(), in.read()));
			msg.setY(getShort(in.read(), in.read()));
			break;
		case SETNAME:
			List<Byte> bytes = new ArrayList<Byte>();
			byte b; 
			while ((b = (byte) in.read()) != 0)
				bytes.add(b);
			byte[] bs = new byte[bytes.size()];
			for (int i = 0; i < bs.length; i++)
				bs[i] = bytes.get(i);
			String name = new String(bs, Charset.forName(CHARSET));
			msg.setName(name);
			return msg;
		}
		in.read(); //Ending 0
		return msg;
	}
	
	private List<Byte> encodeToList(Object... data)
			throws UnsupportedDataTypeException {

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
				throw new UnsupportedDataTypeException("Only byte and short data types are supported");
		}
		return out;
	}
			
	private List<Byte> encodeToList(Message msg) {
		final byte zero = 0, end = zero;
		try {
			byte type = (byte) msg.getType().getIndex();
			Color color = msg.getColor();
			byte r=0, g=0, b=0;
			if (color != null) {
				r = (byte) color.getRed();
				g = (byte) color.getGreen();
				b = (byte) color.getBlue();
			}

			switch (msg.getType()) {
			case LOGINFANOUT: 
				return encodeToList(msg.getUserIndex(), type, end);
			case LOGINRESPONSE:
				List<Byte> all = encodeToList(msg.getUserIndex(), type, msg.getWidth(), msg.getHeight());
				
				for (Message m : msg.getEvents())
					if (m.getType() != MessageType.LOGINRESPONSE)
						all.addAll(encodeToList(m));

				all.addAll(encodeToList(end));
				return all;
			case SETNAME:
				byte[] name = msg.getName().getBytes(Charset.forName(CHARSET));
				all = encodeToList(msg.getUserIndex(), type);
				for (byte bt : name)
					all.add(bt);
				all.addAll(encodeToList(end));
				return all;
			case PICKCOLOR:
				return encodeToList(msg.getUserIndex(), type, r, g, b, end);
			case PENDOWN:
			case PENMOVE:
				if (isServer)
					return encodeToList(msg.getUserIndex(), type, r, g, b, msg.isErase(), msg.getX(), msg.getY(), end);
				else
					return encodeToList(msg.getUserIndex(), type, msg.isErase(), msg.getX(), msg.getY(), end);
			case CLEAR:
			case LOGOUTFANOUT:
			case HEARTBEAT:
				return encodeToList(msg.getUserIndex(), type, end);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return new ArrayList<Byte>();
	}
	
	private byte[] encode(Message msg) {
		List<Byte> e = encodeToList(msg);
		byte[] q = new byte[e.size()];
		for (int i = 0; i < q.length; i++)
			q[i] = e.get(i);
		return q;
	}
	
	public boolean send(Message msg) {
		try {
			out.write(encode(msg));
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public static void main(String[] args) {
		logger.info("Scribble codec test");
		Message msg = new Message(MessageType.PENDOWN, (short) 45);
		msg.setX((short) 268);
		msg.setY((short) 3000);
		msg.setColor(Color.MAGENTA);
		msg.setName("Andrey Panarin");
		msg.setWidth((short) 800);
		msg.setHeight((short) 600);
		ScribbleProcessor proc = new ScribbleProcessor(true);

		for (MessageType type : MessageType.values()) {
			msg.setType(type);
			byte[] out = proc.encode(msg);
			logger.info("Type: {}", type);
			logger.info("Content: {}", Arrays.toString(out));
		}
		
		byte[] array = 
			new byte[] {34, (byte) 255, (byte) 128, 2, (byte) 224, 1,
				12, 1, 0,
				12, 3, (byte) 128, 64, (byte) 255, 100, 0, (byte) 200, 0, 0,
				12, 4, (byte) 128, 64, (byte) 255, 44,  1, (byte) 150, 0, 0,
				12, 5, 0,
				12, 6, 0,
				0};
		InputStream in = new ByteArrayInputStream(array);
		proc.setInputStream(in);
		try {
			logger.info("{}", proc.read());
		} catch (IOException e) {
			logger.error("", e);
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
