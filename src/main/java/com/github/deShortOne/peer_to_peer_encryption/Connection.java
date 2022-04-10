package com.github.deShortOne.peer_to_peer_encryption;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.net.Socket;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import javafx.scene.control.TextArea;

public class Connection {

	private Socket socket;

	private CryptMessage cm;
	private DataOutputStream output;

	private MessagePage mp;

	private PublicKey pubKey;

	private ConnectionRecieve rm;

	private TextArea inputoutput;

	public Connection(MessagePage mp, CryptMessage cm, Socket socket) {
		this.mp = mp;
		this.cm = cm;
		this.socket = socket;
		setupMsgWindow();
	}

	public void setPublicKey(PublicKey pubKey) {
		this.pubKey = pubKey;
		mp.addConnection(rm.getName(), this); // Now handled in KnockKnock
	}

	protected void setUpReciever() {
		rm = new ConnectionRecieve(this, socket, mp, cm);
		Thread t = new Thread(rm);
		t.start();
		try {
			t.join();
			checkAddressBook();
		} catch (InterruptedException e) {
			System.err.println("Connection error");
		} catch (InvalidKeySpecException e) {
			System.err.println("Invalid security key");
		} catch (IOException e) {
			System.err.println("Connection error");
		}
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

	public void recieveMessage(String msg) {
		inputoutput.appendText(msg + "\n");
	}

	public TextArea getMsgWindow() {
		return inputoutput;
	}

	private void sendMessage(byte[] msg) throws IOException {
		if (output != null) {
			output.writeInt(msg.length);
			output.write(msg);
		}
	}

	private void setupMsgWindow() {
		inputoutput = new TextArea();
		inputoutput.setEditable(false);
		inputoutput.setWrapText(true);
	}

	private void checkAddressBook()
			throws InvalidKeySpecException, IOException {
		File f = new File(mp.getName() + "/" + rm.getName() + ".txt");
		if (f.exists()) {
			PublicKey contactsPubKey = RSAEncryption
					.getPublicKeyFromFile(f.toPath());

			if (!pubKey.equals(contactsPubKey)) {
				// Same name but different public key!!
				recieveMessage("Warning: same username but security key has changed");
			}
		} else {
			// New contact
			recieveMessage("Warning: new contact");
		}
	}
}
