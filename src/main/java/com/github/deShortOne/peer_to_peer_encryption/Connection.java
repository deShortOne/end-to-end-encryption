package com.github.deShortOne.peer_to_peer_encryption;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connection {

	Socket socket;
	ServerSocket server;
	int port = 8080;
	
	/**
	 * Server port number. Only specific server.
	 * @param port
	 * @throws IOException
	 */
	public Connection(int port) throws IOException {
		server = new ServerSocket(port);
		socket = server.accept();
		System.out.println("I'm a server");
		
		// testServer();
	}
	
	// Client
	public Connection(InetAddress toAddress) throws IOException {
		this(toAddress, 8080);
	}
	
	// Client
	public Connection(InetAddress toAddress, int port) throws IOException {
		socket = new Socket(toAddress, port);
		System.out.println("I'm the client");
		
		// testClient();
	}
	
	/**
	 * Attempts to create server 
	 * @throws IOException
	 */
	public Connection() throws IOException {
		try {
			server = new ServerSocket(port);
			socket = server.accept();
			System.out.println("I'm a server");
		} catch (BindException e) {
			socket = new Socket(InetAddress.getLoopbackAddress(), 8080);
			System.out.println("I'm the client");
		}
		
//		if (isServer()) {
//			testServer();
//		} else {
//			testClient();
//		}
	}
	
	public boolean sendTo(String hostname, int port) {
	    boolean sent = false;

	    try {
	        Socket socket = createSocket();
	        OutputStream out = socket.getOutputStream();
	        out.write(new byte[1001]);
	        socket.close();
	        sent = true;
	    } catch (UnknownHostException e) {
	        // TODO
	    } catch (IOException e) {
	        // TODO
	    }

	    return sent;
	}

	protected Socket createSocket() throws IOException {
	    return new Socket(InetAddress.getLoopbackAddress(), 8080);
	}
	
	private boolean isServer() {
		return server != null;
	}
	
	private void testClient() throws IOException {
		OutputStream output = socket.getOutputStream();
		byte[] data = "HI\n".getBytes();
		output.write(data);
		
		InputStream input = socket.getInputStream();
		InputStreamReader isr = new InputStreamReader(input);
		BufferedReader br = new BufferedReader(isr);
		String answer = br.readLine();
		
		System.out.println("Message sent from server: " + answer);
	}
	
	private void testServer() throws IOException {
		InputStream is = socket.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String answer = br.readLine();
		String reply;
		if (answer.equals("HI")) {
			reply = "Hi there too!";
		} else {
			reply = "Whaa...";
		}
		
		OutputStream os = socket.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);
		bw.write(reply);
		bw.newLine();
		System.out.println("Message replied to client: " + reply);
		bw.flush();
	}
}
