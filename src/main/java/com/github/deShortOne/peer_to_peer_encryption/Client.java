package com.github.deShortOne.peer_to_peer_encryption;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.baeldung.encryption.CryptMessage;
import com.baeldung.encryption.RSAEncryption;

public class Client extends Exchange {

	/**
	 * Name of user of the client.
	 */
	private String name;

	/**
	 * Thread that listens to server.
	 */
	private Thread serverListener;

	private Account serverConnection;

	private CryptMessage cm;

	/**
	 * Name + conversation page linked to it. Name should be class called Object
	 * which holds other information like public key
	 */
	private HashMap<Account, ConversationPage> messages = new HashMap<>();

	private MessageWindow mw;

	/**
	 * Creates client object which establishes connection to server.
	 * 
	 * @param name of user of client
	 * @throws IOException
	 */
	public Client(String name, MessageWindow mw)
			throws IOException, ConnectException {
		System.out.println("Client started " + name);
		this.name = name;
		this.mw = mw;

		try {
			cm = new CryptMessage(new RSAEncryption(name, name, true));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidKeySpecException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		establishConnectionToServer();
		listenToServer();
	}

	/**
	 * Send message.
	 * 
	 * @param msg
	 * @throws IOException
	 */
	public void sendMessage(Account recipetent, String msg) throws IOException {
		messages.get(recipetent).addText(msg);
		// TODO Encrypt it!
		super.sendMessage(recipetent.getName().getBytes());
		super.sendMessage(msg.getBytes());
	}

	public ConversationPage getMessages(String name) {
		return messages.get(new Account(name, null));
	}

	/**
	 * Makes friend request to server and returns true, returns false if server
	 * cannot find reciptent.
	 * 
	 * @param recipitent name of person to add
	 * @throws IOException
	 */
	public void addFriend(String recipitent) throws IOException {
		super.sendMessage(MessageType.NEWFRIEND.name().getBytes());
		super.sendMessage(recipitent.getBytes());
	}

	/**
	 * Stops listening to server.
	 */
	public void exit() {
		serverListener.interrupt();
		// TODO tells server that connection is FIN
	}

	/**
	 * Creates connection to server. Receives public key, sends name and public
	 * key using server's public key.
	 * 
	 * @throws IOException
	 */
	private void establishConnectionToServer() throws IOException {
		Socket socket = new Socket(InetAddress.getLoopbackAddress(), 8080);
		super.setSocket(socket);

		try {
			serverConnection = new Account("",
					CryptMessage.createPublicKey(super.recieveMessage()));
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		super.sendMessage(serverConnection.encryptMessage(name.getBytes()));
		super.sendMessage(serverConnection.encryptMessage(cm.getPublicKey()));

	}

	/**
	 * Listens to server.
	 */
	private void listenToServer() {
		serverListener = new Thread(() -> {
			while (true) {
				try {
					byte[] senderB = super.recieveMessage();
					byte[] msgInTmpB = super.recieveMessage();

					// decode both sender and msgInTmp

					String sender = new String(senderB, StandardCharsets.UTF_8);
					String msg = new String(msgInTmpB, StandardCharsets.UTF_8);

					Account accToFind = new Account(sender, null);
					if (messages.containsKey(accToFind)) {
						messages.get(accToFind).addText(msg);
					} else if (sender.equals(MessageType.NEWFRIEND.name())) {
						PublicKey pubKey = null;

						try {
							pubKey = CryptMessage
									.createPublicKey(super.recieveMessage());
						} catch (InvalidKeySpecException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						if (pubKey == null) {
							System.err.println("Invalid pubkey>>>>>>>>>>>>>>>>>>>.");
							return;
						}

						Account acc = new Account(msg, pubKey);
						if (messages.containsKey(accToFind)) {
							messages.remove(accToFind);
							messages.put(acc, new ConversationPage());
							messages.get(acc).addText(
									msg + " has recieved your invite!");
							System.out.println(name + " contains!");
						} else {
							messages.put(acc, new ConversationPage());
							messages.get(acc).addText(msg
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
		});
		serverListener.start();
	}
}
