package com.github.deShortOne.peer_to_peer_encryption;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client extends Exchange {

	public Client() throws IOException {
		System.out.println("Client started");
		Socket socket = new Socket(InetAddress.getLoopbackAddress(), 8080);
		
		super.setSocket(socket);
		byte[] msgInTmp = super.recieveMessage();
		System.out.println(new String(msgInTmp, StandardCharsets.UTF_8));
	}
	
	public static void main(String[] args) throws IOException {
		new Client();
	}
}
