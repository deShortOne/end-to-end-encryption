package com.github.deShortOne.peer_to_peer_encryption;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Server {

	/**
	 * Name + connection of that user.
	 * Name should be replaced with contact.
	 */
	private HashMap<String, Exchange> addressBook = new HashMap<>();

	private ServerSocket server;

	private Thread newConnections;
	private Thread listeningToCurrentConnections;

	/**
	 * Creates server.
	 * 
	 * @throws IOException
	 */
	public Server() throws IOException {
		System.out.println("Server start");
		
		try {
			server = new ServerSocket(8080);
		} catch (BindException e) {
			System.err.println("Server already exists");
			System.exit(0);
		}
		
		setupConnections();
	}

	/**
	 * Stops server
	 */
	public void stop() {
		newConnections.interrupt();
		listeningToCurrentConnections.interrupt();
		System.out.println("Server stop");
	}

	/**
	 * Creates new connection with new clients.
	 */
	private void setupConnections() {
		newConnections = new Thread(() -> {
			while (true) {
				try {
					Socket socket = server.accept();
					Exchange ex = new Exchange(socket);

					String name = new String(ex.recieveMessage(),
							StandardCharsets.UTF_8);
					addressBook.put(name, ex);
					setupListening(name, ex);

				} catch (IOException e) {
					e.printStackTrace();
					try {
						server.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;
				}
			}
		});
		newConnections.start();
	}

	/**
	 * Listens for client.
	 * 
	 * @param name	name of user of client
	 * @param ex	
	 */
	private void setupListening(String name, Exchange ex) {
		listeningToCurrentConnections = new Thread(() -> {
			while (true)
				try {
					byte[] msgType = ex.recieveMessage();
					byte[] inMsg = ex.recieveMessage(); // Encrypted with
														// recipients public key

					// Decode msgType using Server's private key
					String sendTo = new String(msgType, StandardCharsets.UTF_8);

					if (addressBook.containsKey(sendTo)) {
						// The person's name
						addressBook.get(sendTo).sendMessage(name.getBytes());
						// name will need to be encrypted
						addressBook.get(sendTo).sendMessage(inMsg);

						System.out.printf("%s send message to %s: %s%n", name,
								sendTo,
								new String(inMsg, StandardCharsets.UTF_8));
					} else {
						System.out.println("Invalid user/ command");
					}

				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
		});
		listeningToCurrentConnections.start();
	}

	public static void main(String[] args) throws IOException {
		new Server();
	}
}
