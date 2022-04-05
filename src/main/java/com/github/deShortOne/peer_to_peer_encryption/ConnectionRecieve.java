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
		
		int lengthOfMessage;
		StringBuilder sb;
		
		try {
			lengthOfMessage = br.read();
			sb = new StringBuilder();
			for (; lengthOfMessage > 0; lengthOfMessage--) {
				sb.append((char) br.read());
			}
			// String answer = br.readLine();
			nameOfOther = sb.toString();
		}  catch (SocketException e1) {
			System.err.println("Connection lost");
			return;
		} catch (IOException e2) {
			e2.printStackTrace();
			return;
		}
		
		while (true) {
			try {
				
				lengthOfMessage = br.read();
				
				sb.setLength(0);
				for (; lengthOfMessage > 0; lengthOfMessage--) {
					sb.append((char) br.read());
				}
				// String answer = br.readLine();
				mp.recieveMessage(nameOfOther + ": " + sb.toString());
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
