package com.github.deShortOne.peer_to_peer_encryption;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Server {

	private HashMap<String, Exchange> addressBook = new HashMap<>();

	private ServerSocket server = new ServerSocket(8080);

	public Server() throws IOException {
		System.out.println("Server start");
		setupConnections();
	}

	private void setupConnections() {
		new Thread(() -> {
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
		}).start();
	}

	private void setupListening(String name, Exchange ex) {
		new Thread(() -> {
			while (true)
				try {
					byte[] msgType = ex.recieveMessage();
					byte[] inMsg = ex.recieveMessage(); // Encrypted with
														// recipients public key

					// Decode msgType using Server's private key
					String sendTo = new String(msgType, StandardCharsets.UTF_8);

					if (addressBook.containsKey(sendTo)) {
						// That person's name
						addressBook.get(sendTo).sendMessage(name.getBytes());
						addressBook.get(sendTo).sendMessage(inMsg);
					}

					System.out.printf("%s send message to %s: %s%n", name,
							sendTo, new String(inMsg, StandardCharsets.UTF_8));
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
		}).start();
	}

	public static void main(String[] args) throws IOException {
		new Server();
	}
}
