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

	public Server() throws IOException {
		setupConnections();
	}

	private void setupConnections() {
		new Thread(() -> {
			while (true) {
				try {
					Socket socket = server.accept();
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
			while (true)
			try {
				byte[] msgType = ex.recieveMessage();
				byte[] inMsg = ex.recieveMessage();
				System.out.println(msgType);
				System.out.println(inMsg);
				
				if (msgType[0] == 'A') {
					System.out.println("Redirect to A");
					addressBook.get("A").sendMessage(name.getBytes());
					addressBook.get("A").sendMessage(inMsg);
				} else {
					System.out.println("Redirect to B");
					addressBook.get("B").sendMessage(name.getBytes());
					addressBook.get("B").sendMessage(inMsg);
				}
				
				System.out.printf("%s send message: %s%n", name,
						new String(inMsg, StandardCharsets.UTF_8));
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
