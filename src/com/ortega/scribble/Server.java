package com.ortega.scribble;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Enumeration;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.net.ServerSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ortega.scribble.context.GreetingContext;
import com.ortega.scribble.data.Message;

public class Server {

	private static final Logger logger = LoggerFactory.getLogger(Server.class);
	
	public static void main(String[] args) throws IOException {
		
		listNetworkInterfaces();
		
		String publicIP = getPublicIPAddress();

		ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket();
		serverSocket.bind(new InetSocketAddress(Constants.DEFAULT_PORT));
		
		if (publicIP != null)
			logger.info("Public IP address: {}", publicIP);
		logger.info("Scribble Server initialized at port {}", Constants.DEFAULT_PORT);
		
		CopyOnWriteArrayList<Message> events = new CopyOnWriteArrayList<Message>();
		GreetingContext context = new GreetingContext(800, 600, events);
		
		new Heart(new NavigableQueue(events));
		
		while (true) {
			Socket in = serverSocket.accept();
			if (in != null)
				logger.info("New connection: "+in.getInetAddress().toString());
			
			new ScribbleProcess(in, context);
		}
	}

	private static void listNetworkInterfaces() {
		try {
			Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
	 
			while (e.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) e.nextElement();
				
				Enumeration<InetAddress> e2 = ni.getInetAddresses();	
				if (e2.hasMoreElements()) {
					boolean foundIpv4 = false;
					do {
						InetAddress ip = (InetAddress) e2.nextElement();
						if (ip instanceof Inet4Address) {
							if (!foundIpv4) {
								foundIpv4 = true;
								logger.info("Interface "+ni.getName());
							}
							logger.info("IP address: "+ ip.toString());
						}
					} while (e2.hasMoreElements());
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	private static String getPublicIPAddress() throws IOException {
		try {
			URL url = new URL("http://checkip.amazonaws.com/");
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String ip = br.readLine();
			br.close();
			return ip;
		} catch (Throwable e) {
			logger.error("Failed to request public IP", e);
			return null;
		}
	}
	
}
