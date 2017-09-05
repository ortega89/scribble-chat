package com.ortega.scribble;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.activity.InvalidActivityException;
import javax.net.SocketFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ortega.scribble.data.Message;
import com.ortega.scribble.data.impl.LoginResponse;
import com.ortega.scribble.data.impl.SetName;
import com.ortega.scribble.gui.ScribbleFrame;


public class Client implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(Client.class);
	
	private String remoteIP;
	private String passedUserName;
		
	public static void main(String[] args) throws UnknownHostException, 
			IOException, InterruptedException {
		String ip = args.length > 0 ? args[0] : null;
		String name = args.length > 1 ? args[1] : null;
		SwingUtilities.invokeLater(new Client(ip, name));
	}

	public Client(String remoteIP, String name) {
		this.remoteIP = remoteIP;
		this.passedUserName = name;
	}

	@Override
	public void run() {
		try {
			final Socket clientSocket = initClient(remoteIP);
			if (clientSocket == null)
				return;
			
			ScribbleProcessor proc = ScribbleProcessor.createClientProcessor(clientSocket);
			LoginResponse loginData = (LoginResponse) proc.read();
			
			String userName = getUserName(passedUserName);
			
			Message msg = new SetName(userName);
			proc.send(msg);
	
			JFrame frame = new ScribbleFrame(loginData, proc);
			
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					try {
						clientSocket.close();
					} catch (IOException e1) {
						logger.error("", e1);
					}
				}
			});
			
			frame.setVisible(true);
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
	public static Socket initClient(String ipString) {
		try {
			byte[] ip = parseIP(ipString);
			return SocketFactory.getDefault().createSocket(Inet4Address.getByAddress(ip), Server.DEFAULT_PORT);
		} catch (ArrayIndexOutOfBoundsException e) {
			JOptionPane.showMessageDialog(null, 
					"Specify the server IP address in your configuration file.", 
					"Unable to connect", JOptionPane.ERROR_MESSAGE);
			return null;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
	
	private static byte[] parseIP(String ipString) throws InvalidActivityException {
		byte[] ip = new byte[4];
		String[] ips = ipString.split("\\.");
		if (ips.length != 4)
			throw new InvalidActivityException("Invalid IP address \""+ipString+"\"");
		
		for (int i = 0; i < 4; i++)
			ip[i] = (byte) Short.parseShort(ips[i]);
		
		return ip;
	}
	
	private static String getUserName(String passedUserName) {
		if (passedUserName != null)
			return passedUserName;
		else
			return JOptionPane.showInputDialog(null, "Please type your name:", System.getProperty("user.name"));
	}
}