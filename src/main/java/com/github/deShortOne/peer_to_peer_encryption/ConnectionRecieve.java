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
		while (true) {
			try {
				
				int lengthOfMessage = br.read();
				
				StringBuilder sb = new StringBuilder();
				for (; lengthOfMessage > 0; lengthOfMessage--) {
					sb.append((char) br.read());
				}
				// String answer = br.readLine();
				mp.recieveMessage("Other person: " + sb.toString());
			} catch (SocketException e1) {
				System.err.println("Connection lost");
				break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}

}
