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
import java.util.Scanner;

public class Connection {

	Socket socket;
	ServerSocket server;
	int port = 8080;
	OutputStream output;
	
	MessagePage mp;
	
	/**
	 * Server port number. Only specific server.
	 * @param port
	 * @throws IOException
	 * end
	 */
	public Connection(int port, MessagePage mp) throws IOException {
		server = new ServerSocket(port);
		socket = server.accept();
		this.mp = mp;
		System.out.println("I'm a server");
		
		// testServer();
		setup();
	}
	
	// Client
	public Connection(InetAddress toAddress, MessagePage mp) throws IOException {
		this(toAddress, 8080, mp);
	}
	
	// Client - end
	public Connection(InetAddress toAddress, int port, MessagePage mp) throws IOException {
		socket = new Socket(toAddress, port);
		this.mp = mp;
		System.out.println("I'm the client");
		setup();
		// testClient();
	}
	
	/**
	 * Attempts to create server 
	 * @throws IOException
	 */
	public Connection(MessagePage mp) throws IOException {
		try {
			server = new ServerSocket(port);
			socket = server.accept();
			System.out.println("I'm a server");
		} catch (BindException e) {
			socket = new Socket(InetAddress.getLoopbackAddress(), 8080);
			System.out.println("I'm the client");
		}
		this.mp = mp;
		setup();
//		if (isServer()) {
//			testServer();
//		} else {
//			testClient();
//		}
	}
	
	public void setup() {
		setUpReciever();
		setUpSender();
	}
	
	private void setUpReciever() {
		ConnectionRecieve rm = new ConnectionRecieve(socket, mp);
		Thread t = new Thread(rm);
		t.start();
	}
	
	private void setUpSender() {
//		try (Scanner in = new Scanner(System.in)) {
//			while (true) {
//				sendMessage(in.nextLine());
//			}
//		} catch (IOException e) {
//			System.err.println("Connection lost");
//		}
		try {
			output = socket.getOutputStream();
		} catch (IOException e1) {
			System.err.println("Cannot connect");
			return;
		}
	}
	
	public void sendMessage(String msg) throws IOException {
		sendMessage(msg.getBytes());
	}
	
	public void sendMessage(byte[] msg) throws IOException {
		output.write(msg);
		output.write("\n".getBytes());
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
