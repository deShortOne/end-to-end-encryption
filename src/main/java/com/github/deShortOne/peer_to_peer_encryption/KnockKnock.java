package com.github.deShortOne.peer_to_peer_encryption;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class KnockKnock {

	private Socket socket;
	private ServerSocket server;
	private int port = 8080;
	private CryptMessage cm;

	private MessagePage mp;

	public KnockKnock(MessagePage mp, CryptMessage cm) throws IOException {
		this.mp = mp;
		this.cm = cm;

		try {
			server = new ServerSocket(port);
			System.out.println("I'm a server");
			setServer();
		} catch (BindException e) {
			socket = new Socket(InetAddress.getLoopbackAddress(), 8080);
			System.out.println("I'm the client");
		}
	}

	public void setServer() {
		mp.setErrorMsg("Not connected to client");

		new Thread() {
			public void run() {
				while (true) {
					Thread t = new Thread() {
						public void run() {
							try {
								socket = server.accept();
								ServerConnection sc = new ServerConnection(mp,
										cm, socket);
								mp.addConnection(mp.getName(), sc);
								mp.setErrorMsg("");
							} catch (IOException e) {

							}
						}
					};
					t.start();
					try {
						t.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					}
				}
			}
		}.start();
	}

	public void setClient() {
		ClientConnection cc = new ClientConnection(mp, cm, socket);
		mp.addConnection(mp.getName(), cc);
	}
}
