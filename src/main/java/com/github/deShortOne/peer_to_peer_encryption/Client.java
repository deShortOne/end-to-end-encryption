package com.github.deShortOne.peer_to_peer_encryption;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client extends Exchange {

	/**
	 * Name of user of the client.
	 */
	private String name;
	
	/**
	 * Thread that listens to server.
	 */
	private Thread serverListener;

	/**
	 * Creates client object which establishes connection to server.
	 * 
	 * @param name			of user of client
	 * @throws IOException	
	 */
	public Client(String name) throws IOException {
		System.out.println("Client started");
		this.name = name;
		establishConnectionToServer();
		listenToServer();
	}

	/**
	 * Send message.
	 * 
	 * @param msg
	 * @throws IOException
	 */
	public void sendMessage(String msg) throws IOException {
		System.out.printf("Client %s sends %s%n", name, msg);
		super.sendMessage(msg.getBytes());
	}
	
	/**
	 * Stops listening to server.
	 */
	public void exit() {
		serverListener.interrupt();
		// TODO tells server that connection is FIN
	}

	/**
	 * Creates connection to server.
	 * @throws IOException
	 */
	private void establishConnectionToServer() throws IOException {
		Socket socket = new Socket(InetAddress.getLoopbackAddress(), 8080);
		super.setSocket(socket);

		super.sendMessage(name.getBytes());
	}
	
	/**
	 * Listens to server.
	 */
	private void listenToServer() {
		serverListener = new Thread(() -> {
			while (true) {
				try {
					byte[] sender = super.recieveMessage();
					byte[] msgInTmp = super.recieveMessage();
					
					// decode both sender and msgInTmp
					
					System.out.printf("%s says to %s: %s%n",
							new String(sender, StandardCharsets.UTF_8),
							this.name,
							new String(msgInTmp, StandardCharsets.UTF_8));
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
		});
		serverListener.start();
	}

	public static void main(String[] args) throws IOException {
		// Run this class twice, replace A with B or vice versa
		new Client("A");
	}
}
