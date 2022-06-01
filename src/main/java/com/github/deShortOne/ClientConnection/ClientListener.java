package com.github.deShortOne.ClientConnection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

import com.baeldung.encryption.CryptMessage;
import com.baeldung.encryption.RSAEncryption;
import com.github.deShortOne.Account.ClientAccount;
import com.github.deShortOne.Account.ServerAccount;
import com.github.deShortOne.end_to_end_encryption.MessageType;
import com.github.deShortOne.end_to_end_encryption.MessageWindow;

public class ClientListener implements Runnable {

	private ServerAccount serverConnection;

	private HashMap<String, ClientAccount> messages;

	private MessageWindow mw;
	
	private CryptMessage cm;

	public ClientListener(ServerAccount serverConnection,
			HashMap<String, ClientAccount> messages, MessageWindow mw,
			CryptMessage cm) {
		this.serverConnection = serverConnection;
		this.messages = messages;
		this.mw = mw;
		this.cm = cm;
	}

	public void run() {
		while (true) {
			try {
				byte[] senderB = serverConnection.recieveMessage();
				byte[] msgInTmpB = serverConnection.recieveMessage();

				// decode both sender and msgInTmp
				String sender = new String(senderB, StandardCharsets.UTF_8);

				if (messages.containsKey(sender)) {
					String msg = new String(cm.recieveMessage(msgInTmpB), StandardCharsets.UTF_8);
					messages.get(sender).addMessage(msg);
				} else if (sender.equals(MessageType.NEWFRIEND.name())) {
					String msg = new String(msgInTmpB, StandardCharsets.UTF_8);
					addFriend(msg);
				}

			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	private void addFriend(String nameOfOther) throws IOException {
		PublicKey pubKey = null;

		try {
			pubKey = CryptMessage
					.createPublicKey(serverConnection.recieveMessage());
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}

		if (pubKey == null) {
			System.err.println("Invalid pubkey>>>>>>>>>>>>>>>>>>>.");
			return;
		}

		ClientAccount newAccount = new ClientAccount(nameOfOther, pubKey,
				new ConversationPage(), serverConnection);

		if (messages.containsKey(nameOfOther)) {
			// TODO move conversation page across
			messages.remove(nameOfOther);

			messages.put(nameOfOther, newAccount);
			messages.get(nameOfOther)
					.addMessage(nameOfOther + " has recieved your invite!");
		} else {
			messages.put(nameOfOther, newAccount);
			messages.get(nameOfOther).addMessage(nameOfOther
					+ " wants to be your friend!\nReply to accept!");
			mw.addContact(nameOfOther);
		}
	}
}
