package com.github.deShortOne.ServerConnection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import com.baeldung.encryption.CryptMessage;
import com.github.deShortOne.Account.ServerAccount;
import com.github.deShortOne.end_to_end_encryption.MessageType;

public class ServerListener implements Runnable {

	private String name;
	private HashMap<String, ServerAccount> addressBook;
	private ServerAccount account;

	public ServerListener(String name, ServerAccount account,
			HashMap<String, ServerAccount> addressBook) {
		this.name = name;
		this.addressBook = addressBook;
		this.account = account;
	}

	public void run() {
		while (true) {
			try {
				byte[] messageTypeOrNameOfRecipitent = account.recieveMessage();
				byte[] messageIn = account.recieveMessage();
				// Encrypted with recipients public key if message for
				// reciptent
				// Encrypted with server's public key if message for server,
				// cause duh

				// Decode msgType using Server's private key
				String sendTo = new String(messageTypeOrNameOfRecipitent,
						StandardCharsets.UTF_8);

				if (addressBook.containsKey(sendTo)) {
					ServerAccount recieveMessageAccount = addressBook
							.get(sendTo);

					// The person's name

					System.out.printf("%s send message to %s: %s%n", name,
							sendTo,
							new String(messageIn, StandardCharsets.UTF_8));
					recieveMessageAccount.sendMessage(name, messageIn);
				} else if (sendTo.equals(MessageType.NEWFRIEND.name())) {
					// decrypt inMsg to find person

					String recieveRequestName = new String(messageIn,
							StandardCharsets.UTF_8);

					if (addressBook.containsKey(recieveRequestName)) {

						// send to new friend request
						ServerAccount receiveRequestAccount = (ServerAccount) addressBook
								.get(recieveRequestName);

						receiveRequestAccount.friendRequest(name.getBytes(),
								account.getPublicKey().getEncoded());

						// send requester information about new person
						// TODO wait for request to be accepted
						account.friendRequest(recieveRequestName.getBytes(),
								receiveRequestAccount.getPublicKey()
										.getEncoded());
					}
					// TODO if person no exist, so communicate that

				} else {
					System.out.println("Invalid user/ command");
				}
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}

}
