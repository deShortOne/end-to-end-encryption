package com.github.deShortOne.peer_to_peer_encryption;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Exchange {
	
	private Server() throws IOException {
		System.out.println("Server started");
		ServerSocket server = new ServerSocket(8080);

		new Thread(() -> {
			int n = 0;
			while (true) {
				Socket socket;
				try {
					socket = server.accept();
					super.setSocket(socket);
					super.sendMessage(("Hi" + n++).getBytes());
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

		System.out.println("Done");
	}

	public static void main(String[] args) throws IOException {
		new Server();
	}
}
