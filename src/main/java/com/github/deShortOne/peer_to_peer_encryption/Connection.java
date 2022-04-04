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
	 * 
	 * @param port
	 * @throws IOException end
	 */
	public Connection(int port, MessagePage mp) throws IOException {
		server = new ServerSocket(port);
		this.mp = mp;
		System.out.println("I'm a server");

		setup();
	}

	// Client
	public Connection(InetAddress toAddress, MessagePage mp)
			throws IOException {
		this(toAddress, 8080, mp);
	}

	// Client - end
	public Connection(InetAddress toAddress, int port, MessagePage mp)
			throws IOException {
		socket = new Socket(toAddress, port);
		this.mp = mp;
		System.out.println("I'm the client");
		setup();
	}

	/**
	 * Attempts to create server
	 * 
	 * @throws IOException
	 */
	public Connection(MessagePage mp) throws IOException {
		try {
			server = new ServerSocket(port);
			System.out.println("I'm a server");
		} catch (BindException e) {
			socket = new Socket(InetAddress.getLoopbackAddress(), 8080);
			System.out.println("I'm the client");
		}
		this.mp = mp;
		setup();
	}

	public void setup() {
		if (isServer()) {
			Thread t = new Thread() {
				public void run() {
					try {
						socket = server.accept();
						setUpReciever();
						setUpSender();
						mp.setErrorMsg("");
					} catch (IOException e) {

					}
				}
			};
			t.start();

		} else {
			setUpReciever();
			setUpSender();
			mp.setErrorMsg("");
		}
	}

	private void setUpReciever() {
		ConnectionRecieve rm = new ConnectionRecieve(socket, mp);
		Thread t = new Thread(rm);
		t.start();
	}

	private void setUpSender() {
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
		if (output != null) {
			output.write(msg);
			output.write("\n".getBytes());
		}
	}

	private boolean isServer() {
		return server != null;
	}
}
