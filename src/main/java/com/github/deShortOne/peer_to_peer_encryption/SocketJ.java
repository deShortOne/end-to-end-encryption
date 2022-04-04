package com.github.deShortOne.peer_to_peer_encryption;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketJ {
	byte[] payload;

	public SocketJ(byte[] payload) {
		this.payload = payload;
	}

	public boolean sendTo(String hostname, int port) {
		boolean sent = false;

		try {
			Socket socket = createSocket();
			OutputStream out = socket.getOutputStream();
			out.write(payload);
			socket.close();
			sent = true;
		} catch (UnknownHostException e) {
			// TODO
		} catch (IOException e) {
			// TODO
		}

		return sent;
	}

	public Socket createSocket() throws IOException {
		return new Socket(InetAddress.getLoopbackAddress(), 8080);
	}
}
