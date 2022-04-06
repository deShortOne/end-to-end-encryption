package com.github.deShortOne.peer_to_peer_encryption;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Connection {

	private Socket socket;
	private ServerSocket server;
	private int port = 8080;
	private CryptMessage cm;
	private DataOutputStream output;

	private MessagePage mp;

	private PublicKey pubKey;

	static byte[] base;
	static byte[] msg;

	/**
	 * Server port number. Only specific server.
	 * 
	 * @param port
	 * @throws IOException end
	 */
	public Connection(int port, MessagePage mp, CryptMessage cm)
			throws IOException {
		server = new ServerSocket(port);
		this.mp = mp;
		this.cm = cm;
		System.out.println("I'm a server");

		setup();
	}

	// Client
	public Connection(InetAddress toAddress, MessagePage mp, CryptMessage cm)
			throws IOException {
		this(toAddress, 8080, mp, cm);
	}

	// Client - end
	public Connection(InetAddress toAddress, int port, MessagePage mp,
			CryptMessage cm) throws IOException {
		socket = new Socket(toAddress, port);
		this.mp = mp;
		this.cm = cm;
		System.out.println("I'm the client");
		setup();
	}

	/**
	 * Attempts to create server
	 * 
	 * @throws IOException
	 */
	public Connection(MessagePage mp, CryptMessage cm) throws IOException {
		try {
			server = new ServerSocket(port);
			System.out.println("I'm a server");
		} catch (BindException e) {
			socket = new Socket(InetAddress.getLoopbackAddress(), 8080);
			System.out.println("I'm the client");
		}
		this.mp = mp;
		this.cm = cm;
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
		ConnectionRecieve rm = new ConnectionRecieve(this, socket, mp, cm);
		Thread t = new Thread(rm);
		t.start();
	}

	private void setUpSender() {
		try {
			OutputStream output = socket.getOutputStream();
			this.output = new DataOutputStream(output);
			sendMessageClear(mp.getName());
			sendMessage(mp.getPublicKey());

		} catch (IOException e1) {
			System.err.println("Cannot connect");
			return;
		}
	}

	private void sendMessageClear(String msg) throws IOException {
		sendMessage(msg.getBytes());
	}

	public void sendMessageEncrypted(String msg) throws IOException {
		try {
			byte[][] encryptedMessage;
			try {
				encryptedMessage = CryptMessage.sendMessage(msg, RSAEncryption.getCommonKey());
			} catch (NoSuchAlgorithmException | InvalidKeySpecException
					| IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			base = encryptedMessage[0];
			this.msg = encryptedMessage[1];

			sendMessage(encryptedMessage[0]);
			sendMessage(encryptedMessage[1]);
		} catch (InvalidKeyException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException
				| InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}

	}

	private void sendMessage(byte[] msg) throws IOException {
		if (output != null) {
			output.writeInt(msg.length);
			output.write(msg);
		}
	}

	private boolean isServer() {
		return server != null;
	}
}
