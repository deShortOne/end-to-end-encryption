package com.github.deShortOne.peer_to_peer_encryption;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;

public class Client extends Exchange {

	// Should be passed in via the constructor
	private String name;

	public Client() throws IOException {
		System.out.println("Client started");

		byte[] nameByte = new byte[3];
		new Random().nextBytes(nameByte);
		name = new String(nameByte, StandardCharsets.UTF_8);

		new Client(name);
	}

	public Client(String name) throws IOException {
		this.name = name;
		establishConnectionToServer();
		listenToServer();

		Scanner in = new Scanner(System.in);
		while (true) {
			String msg = in.nextLine();
			sendMessage(msg);
		}
	}

	public void sendMessage(String msg) throws IOException {
		System.out.printf("Client %s sends %s%n", name, msg);
		super.sendMessage(msg.getBytes());
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
					byte[] sender = super.recieveMessage();
					byte[] msgInTmp = super.recieveMessage();
					System.out.printf("%s says: %s%n",
							new String(sender, StandardCharsets.UTF_8),
							new String(msgInTmp, StandardCharsets.UTF_8));
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
		}).start();
	}

	public static void main(String[] args) throws IOException {
		// Run this class twice, replace A with B or vice versa
		new Client("A");
	}
}
