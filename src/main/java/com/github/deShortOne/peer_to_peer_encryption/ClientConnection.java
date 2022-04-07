package com.github.deShortOne.peer_to_peer_encryption;

import java.net.Socket;

public class ClientConnection extends Connection {

	public ClientConnection(MessagePage mp, CryptMessage cm, Socket socket) {
		super(mp, cm, socket);

		System.out.println("Setting up sender");
		super.setUpSender();
		System.out.println("=End");
		super.setUpReciever();
		
		System.out.println("I am client - " + mp.getName());
	}
	
}
