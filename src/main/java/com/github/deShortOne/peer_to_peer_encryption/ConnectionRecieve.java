package com.github.deShortOne.peer_to_peer_encryption;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class ConnectionRecieve implements Runnable {

	Socket socket;
	MessagePage mp;
	String nameOfOther;

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
		}  catch (SocketException e1) {
			System.err.println("Connection lost");
			return;
		} catch (IOException e2) {
			e2.printStackTrace();
			return;
		}
		System.out.println("Ready");
		while (true) {
			try {
				System.out.println(1);
				String s = getMessage(br);
				System.out.println(2);
				mp.recieveMessage(nameOfOther + ": " + s);
				System.out.println(3);
			} catch (SocketException e1) {
				System.err.println("Connection lost");
				break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	private String getMessage(BufferedReader br) throws IOException {
		int lengthOfMessage;
		StringBuilder sb = new StringBuilder();
		
		lengthOfMessage = br.read();
		String s = "";
		if (lengthOfMessage == 0) {
			while ((lengthOfMessage = br.read()) != 10) {
				s += String.valueOf(lengthOfMessage);
			}
			lengthOfMessage = Integer.valueOf(s);
		}
		
		sb = new StringBuilder();
		for (; lengthOfMessage > 0; lengthOfMessage--) {
			sb.append((char) br.read());
		}

		return sb.toString();
	}
}
