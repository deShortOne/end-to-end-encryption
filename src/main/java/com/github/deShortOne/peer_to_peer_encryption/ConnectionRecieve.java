package com.github.deShortOne.peer_to_peer_encryption;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ConnectionRecieve implements Runnable {

	private Connection connection;
	private Socket socket;
	private MessagePage mp;
	private CryptMessage cm;
	private String nameOfOther;

	public ConnectionRecieve(Connection connection, Socket socket,
			MessagePage mp, CryptMessage cm) {
		this.connection = connection;
		this.socket = socket;
		this.mp = mp;
		this.cm = cm;
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
			nameOfOther = getMessageClear(dis);
			connection.setPublicKey(getPublicKey(dis));
			System.out.println("Key created");
		} catch (SocketException e1) {
			System.err.println("Connection lost");
			return;
		} catch (IOException e2) {
			e2.printStackTrace();
			return;
		} catch (InvalidKeySpecException e) {
			System.err.println("Invalid key");
			return;
		}

		while (true) {
			try {
				byte[] base = getBytes(dis);
				byte[] cipherMessage = getBytes(dis);
				mp.recieveMessage(nameOfOther + ": "
						+ cm.recieveMessage(base, cipherMessage));
			} catch (SocketException e) {
				System.err.println("Connection lost");
				break;
			} catch (IOException e1) {
				e1.printStackTrace();
				break;
			} catch (InvalidKeyException | NoSuchAlgorithmException
					| NoSuchPaddingException | IllegalBlockSizeException
					| BadPaddingException e2) {
				e2.printStackTrace();
				break;
			}
		}
	}

	private PublicKey getPublicKey(DataInputStream dis)
			throws IOException, InvalidKeySpecException {
		return RSAEncryption.createPublicKey(getBytes(dis));
	}

	private String getMessageClear(DataInputStream dis) throws IOException {
		return new String(getBytes(dis), StandardCharsets.UTF_8);
	}

	private byte[] getBytes(DataInputStream dis) throws IOException {
		int lengthOfMessage = dis.readInt();

		byte[] arr = new byte[lengthOfMessage];
		dis.readFully(arr);
		return arr;
	}
}
