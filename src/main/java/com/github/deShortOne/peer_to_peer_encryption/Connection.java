package com.github.deShortOne.peer_to_peer_encryption;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;

public class Connection {

	private Socket socket;
	private ServerSocket server;
	private int port = 8080;
	private OutputStream output;

	private MessagePage mp;

	private PublicKey pubKey;

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

	public void setPublicKey(PublicKey pubKey) {
		this.pubKey = pubKey;
	}

	public void setup() {
		if (isServer()) {
			new Thread() {
				public void run() {
					try {
						mp.setErrorMsg("Not connected to client");
						socket = server.accept();
						setUpSender();
						setUpReciever();
						mp.setErrorMsg("");
					} catch (IOException e) {

					}
				}
			}.start();

		} else {
			setUpReciever();
			setUpSender();
			mp.setErrorMsg("");
		}
	}

	private void setUpReciever() {
		ConnectionRecieve rm = new ConnectionRecieve(this, socket, mp);
		Thread t = new Thread(rm);
		t.start();
	}

	private void setUpSender() {
		try {
			output = socket.getOutputStream();
			sendMessage(mp.getName());

			sendMessage(mp.getPublicKey());

		} catch (IOException e1) {
			System.err.println("Cannot connect");
			return;
		}
	}

	public void sendMessage(String msg) throws IOException {
		sendMessage(msg.getBytes());
	}
	
	private void sendMessage(byte[] msg) throws IOException {
		
		DataOutputStream dis = new DataOutputStream(output);
		
		if (output != null) {
			dis.writeInt(msg.length);
			dis.write(msg);
		}
	}

	private boolean isServer() {
		return server != null;
	}
}
