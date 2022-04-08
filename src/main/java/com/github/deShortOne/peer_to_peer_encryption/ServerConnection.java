package com.github.deShortOne.peer_to_peer_encryption;

import java.net.Socket;

public class ServerConnection extends Connection {

	public ServerConnection(MessagePage mp, CryptMessage cm, Socket socket) {
		super(mp, cm, socket);

		super.setUpReciever();
		super.setUpSender();
		
		System.out.println("I am server - " + mp.getName());
	}
}
