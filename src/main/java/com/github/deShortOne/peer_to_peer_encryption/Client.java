package com.github.deShortOne.peer_to_peer_encryption;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

import javax.crypto.NoSuchPaddingException;

import com.baeldung.encryption.CryptMessage;
import com.baeldung.encryption.RSAEncryption;

public class Client {

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
	private HashMap<String, ClientAccount> messages = new HashMap<>();

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
	public void sendMessage(String name, String msg) throws IOException {
		ClientAccount recievingPerson = messages.get(name);
		recievingPerson.addMessage(msg);

		recievingPerson.sendMessage(name.getBytes());
		recievingPerson.sendMessage(msg.getBytes());

	}

	public ConversationPage getMessages(String name) {
		return messages.get(name).getConversationPage();
	}

	/**
	 * Makes friend request to server and returns true, returns false if server
	 * cannot find reciptent.
	 * 
	 * @param recipitent name of person to add
	 * @throws IOException
	 */
	public void addFriend(String recipitent) throws IOException {
		serverConnection.sendMessage(MessageType.NEWFRIEND.name().getBytes());
		serverConnection.sendMessage(recipitent.getBytes());
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
		Exchange ex = new Exchange(socket);

		try {
			serverConnection = new ServerAccount(
					CryptMessage.createPublicKey(ex.recieveMessage()), ex);
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		serverConnection.sendMessage(name.getBytes());
		serverConnection.sendMessage(cm.getPublicKey());
		System.out.println("Client done");
	}

	/**
	 * Listens to server.
	 */
	private void listenToServer() {
		serverListener = new Thread(() -> {
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
							System.err.println(
									"Invalid pubkey>>>>>>>>>>>>>>>>>>>.");
							return;
						}

						ClientAccount newAccount = new ClientAccount(null,
								new ConversationPage(), serverConnection.getExchange());

						if (messages.containsKey(msg)) {
							messages.remove(msg);

							messages.put(msg, newAccount);
							messages.get(msg).addMessage(
									msg + " has recieved your invite!");
							System.out.println(name + " contains!");
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
		});
		serverListener.start();
	}
}
