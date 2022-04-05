package com.github.deShortOne.peer_to_peer_encryption;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.security.PublicKey;

public class ConnectionRecieve implements Runnable {

	private Socket socket;
	private MessagePage mp;
	private String nameOfOther;

	public ConnectionRecieve(Socket socket, MessagePage mp) {
		this.socket = socket;
		this.mp = mp;
	}

	@Override
	public void run() {
		InputStream is;
		try {
			is = socket.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);

		try {
			nameOfOther = getMessage(br);

		} catch (SocketException e1) {
			System.err.println("Connection lost");
			return;
		} catch (IOException e2) {
			e2.printStackTrace();
			return;
		}
		while (true) {
			try {
				mp.recieveMessage(nameOfOther + ": " + getMessage(br));
			} catch (SocketException e1) {
				System.err.println("Connection lost");
				break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	private PublicKey getPublicKey(BufferedReader br) {
		
		return null;
	}

	private String getMessage(BufferedReader br) throws IOException {
		int lengthOfMessage = getMessageLength(br);

		StringBuilder sb = new StringBuilder();
		for (; lengthOfMessage > 0; lengthOfMessage--) {
			sb.append((char) br.read());
		}

		return sb.toString();
	}

	private int getMessageLength(BufferedReader br) throws IOException {
		int lengthOfMessage = br.read();
		if (lengthOfMessage == 0) {
			StringBuilder sb = new StringBuilder();
			while ((lengthOfMessage = br.read()) != 10) {
				sb.append(lengthOfMessage);
			}
			lengthOfMessage = Integer.valueOf(sb.toString());
		}
		return lengthOfMessage;
	}
}
