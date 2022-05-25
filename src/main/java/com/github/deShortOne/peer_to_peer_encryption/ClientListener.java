package com.github.deShortOne.peer_to_peer_encryption;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

import com.baeldung.encryption.CryptMessage;

public class ClientListener implements Runnable {

	private ServerAccount serverConnection;

	private HashMap<String, ClientAccount> messages;

	private MessageWindow mw;
	
	public ClientListener(ServerAccount serverConnection,
			HashMap<String, ClientAccount> messages, MessageWindow mw) {
		this.serverConnection = serverConnection;
		this.messages = messages;
		this.mw = mw;
	}

	public void run() {
		while (true) {
			try {
				byte[] senderB = serverConnection.recieveMessage();
				byte[] msgInTmpB = serverConnection.recieveMessage();

				// decode both sender and msgInTmp

				String sender = new String(senderB, StandardCharsets.UTF_8);
				String msg = new String(msgInTmpB, StandardCharsets.UTF_8);

				if (messages.containsKey(sender)) {
					messages.get(sender).addMessage(msg);
				} else if (sender.equals(MessageType.NEWFRIEND.name())) {
					PublicKey pubKey = null;

					try {
						pubKey = CryptMessage.createPublicKey(
								serverConnection.recieveMessage());
					} catch (InvalidKeySpecException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (pubKey == null) {
						System.err
								.println("Invalid pubkey>>>>>>>>>>>>>>>>>>>.");
						return;
					}

					ClientAccount newAccount = new ClientAccount(msg, null,
							new ConversationPage(), serverConnection);

					if (messages.containsKey(msg)) {
						messages.remove(msg);

						messages.put(msg, newAccount);
						messages.get(msg)
								.addMessage(msg + " has recieved your invite!");
//						System.out.println(name + " contains!");
					} else {
						messages.put(msg, newAccount);
						messages.get(msg).addMessage(msg
								+ " wants to be your friend!\nReply to accept!");
						System.out.println("New person!");
						mw.addContact(msg);
					}

				}

			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}
}
