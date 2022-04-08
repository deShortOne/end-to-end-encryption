package com.github.deShortOne.peer_to_peer_encryption;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Connection {

	private Socket socket;
	
	private CryptMessage cm;
	private DataOutputStream output;

	private MessagePage mp;

	private PublicKey pubKey;
	
	private ConnectionRecieve rm;
	
	public Connection(MessagePage mp, CryptMessage cm, Socket socket) {
		this.mp = mp;
		this.cm = cm;
		this.socket = socket;
	}

	public void setPublicKey(PublicKey pubKey) {
		this.pubKey = pubKey;
		mp.addConnection(rm.getName(), this); // Now handled in KnockKnock
	}

	protected void setUpReciever() {
		rm = new ConnectionRecieve(this, socket, mp, cm);
		Thread t = new Thread(rm);
		t.start();
	}

	protected void setUpSender() {
		try {
			OutputStream output = socket.getOutputStream();
			this.output = new DataOutputStream(output);
			sendMessageClear(mp.getName());
			sendMessage(mp.getPublicKey());

		} catch (IOException e1) {
			System.err.println("Cannot connect");
			return;
		}
	}

	private void sendMessageClear(String msg) throws IOException {
		sendMessage(msg.getBytes());
	}

	public void sendMessageEncrypted(String msg) throws IOException {
		try {
			byte[][] encryptedMessage;

			encryptedMessage = CryptMessage.sendMessage(msg, pubKey);
			
			sendMessage(encryptedMessage[0]);
			sendMessage(encryptedMessage[1]);
		} catch (InvalidKeyException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException
				| InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
	}

	private void sendMessage(byte[] msg) throws IOException {
		if (output != null) {
			output.writeInt(msg.length);
			output.write(msg);
		}
	}
}
