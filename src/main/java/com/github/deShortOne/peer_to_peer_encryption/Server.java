package com.github.deShortOne.peer_to_peer_encryption;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Server {

	private HashMap<String, Exchange> addressBook = new HashMap<>();

	private ServerSocket server = new ServerSocket(8080);
	private int n = 0;

	private Server() throws IOException {
		System.out.println("Server started");

		setupConnections();

		System.out.println("Done");
	}

	private void setupConnections() {
		new Thread(() -> {
			while (true) {
				Socket socket;
				try {
					socket = server.accept();
					Exchange ex = new Exchange(socket);
					ex.sendMessage(("Hi" + n++).getBytes());

					byte[] name = ex.recieveMessage();
					addressBook.put(new String(name, StandardCharsets.UTF_8),
							ex);
					setupListening(new String(name, StandardCharsets.UTF_8), ex);
					
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
			try {
				byte[] inMsg = ex.recieveMessage();
				System.out.printf("%s send message: %s", name,
						new String(inMsg, StandardCharsets.UTF_8));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	public static void main(String[] args) throws IOException {
		new Server();
	}
}
