package com.github.deShortOne.peer_to_peer_encryption;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

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
		DataInputStream dis = new DataInputStream(is);

		try {
			nameOfOther = getMessage(dis);
			getPublicKey(dis);
			System.out.println("End key created!!!");
		} catch (SocketException e1) {
			System.err.println("Connection lost");
			return;
		} catch (IOException e2) {
			e2.printStackTrace();
			return;
		}
		while (true) {
			try {
				mp.recieveMessage(nameOfOther + ": " + getMessage(dis));
			} catch (SocketException e1) {
				System.err.println("Connection lost");
				break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	private PublicKey getPublicKey(DataInputStream dis) throws IOException {

		int lengthOfMessage = getMessageLength(dis);
		
		byte[] out = new byte[lengthOfMessage];
		dis.readFully(out);
		
		try {
			return RSAEncryption.createPublicKey(out); //getMessage(br).getBytes()
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			System.err.println("Invalid key");
			return null;
		}
	}

	private String getMessage(DataInputStream dis) throws IOException {
		int lengthOfMessage = getMessageLength(dis);

		byte[] arr = new byte[lengthOfMessage];
		dis.readFully(arr);
		
		return dis.toString();
	}

	private int getMessageLength(DataInputStream dis) throws IOException {
		
		int lengthOfMessage = dis.readInt();
		if (lengthOfMessage == 0) {
			StringBuilder sb = new StringBuilder();
			while ((lengthOfMessage = dis.readInt()) != 10) {
				sb.append(lengthOfMessage);
			}
			lengthOfMessage = Integer.valueOf(sb.toString());
		}
		return lengthOfMessage;
	}
}
