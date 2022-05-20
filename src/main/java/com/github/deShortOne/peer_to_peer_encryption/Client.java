package com.github.deShortOne.peer_to_peer_encryption;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Client extends Exchange {

	private static Random random = new Random();
	
	// Should be passed in via the constructor
	private String name;

	public Client() throws IOException {
		System.out.println("Client started");

		byte[] nameByte = new byte[3];
		new Random().nextBytes(nameByte);
		name = new String(nameByte, StandardCharsets.UTF_8);
		
		establishConnectionToServer();
		super.sendMessage("bye bye!".getBytes());
	}
	
	public void sendMessage(String msg) throws IOException {		
		byte[] msgByte = new byte[3];
		super.sendMessage(msgByte);
	}

	private void establishConnectionToServer() throws IOException {
		Socket socket = new Socket(InetAddress.getLoopbackAddress(), 8080);
		super.setSocket(socket);
		
		byte[] msgInTmp = super.recieveMessage();
		System.out.println(new String(msgInTmp, StandardCharsets.UTF_8));
		
		super.sendMessage(name.getBytes());
	}

	private void listenToServer() {
		new Thread(() -> {
			while (true) {
				try {
					byte[] msgInTmp = super.recieveMessage();
					System.out.println(new String(msgInTmp, StandardCharsets.UTF_8));
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
		}).start();
	}

	public static void main(String[] args) throws IOException {
		new Client();
	}
}
