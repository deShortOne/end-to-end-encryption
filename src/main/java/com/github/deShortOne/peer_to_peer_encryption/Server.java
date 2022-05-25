package com.github.deShortOne.peer_to_peer_encryption;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

import javax.crypto.NoSuchPaddingException;

import com.baeldung.encryption.CryptMessage;
import com.baeldung.encryption.RSAEncryption;

public class Server {

	/**
	 * Name + connection of that user. Name should be replaced with contact.
	 */
	private HashMap<String, Account> addressBook = new HashMap<>();

	private ServerSocket server;

	private Thread newConnections;
	private Thread listeningToCurrentConnections;

	private CryptMessage cm;

	/**
	 * Creates server.
	 * 
	 * @throws IOException
	 */
	public Server() throws IOException {
		System.out.println("Server start");

		try {
			cm = new CryptMessage(new RSAEncryption("Server", "server", true));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidKeySpecException | IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		}

		try {
			server = new ServerSocket(8080);
		} catch (BindException e) {
			System.err.println("Server already exists");
			System.exit(0);
		}

		setupConnections();
	}

	/**
	 * Stops server
	 */
	public void stop() {
		newConnections.interrupt();
		listeningToCurrentConnections.interrupt();
		System.out.println("Server stop");
	}

	private byte[] recieveEncryptedMessage(Exchange ex) throws IOException {
		byte[] encryptedMsg = ex.recieveMessage();

		return recieveEncryptedMessage(encryptedMsg);
	}

	private byte[] recieveEncryptedMessage(byte[] encryptedMsg) {
		return cm.recieveMessage(encryptedMsg);
	}

	/**
	 * Creates new connection with new clients.
	 */
	private void setupConnections() {
		newConnections = new Thread(() -> {
			while (true) {
				try {
					Socket socket = server.accept();
					Exchange ex = new Exchange(socket);

					ex.sendMessage(cm.getPublicKey());

					String name = new String(recieveEncryptedMessage(ex),
							StandardCharsets.UTF_8);
					PublicKey pubKey = null;
					try {
						pubKey = CryptMessage
								.createPublicKey(recieveEncryptedMessage(ex));
					} catch (InvalidKeySpecException e) {
						e.printStackTrace();
					}

					Account acc = new ServerAccount(pubKey, ex);
					addressBook.put(name, acc);
					setupListening(name, ex, acc);
					System.out.println("New connection from " + name + " "
							+ addressBook.size());

				} catch (IOException e) {
					e.printStackTrace();
					try {
						server.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;
				}
			}
		});
		newConnections.start();
	}

	/**
	 * Listens for client.
	 * 
	 * @param acc name of user of client
	 * @param ex
	 */
	private void setupListening(String name, Exchange ex, Account account) {
		listeningToCurrentConnections = new Thread(() -> {
			while (true) {
				try {
					byte[] messageTypeOrNameOfRecipitent = ex.recieveMessage();
					byte[] messageIn = ex.recieveMessage();
					// Encrypted with recipients public key if message for
					// reciptent
					// Encrypted with server's public key if message for server,
					// cause duh

					// Decode msgType using Server's private key
					String sendTo = new String(messageTypeOrNameOfRecipitent, StandardCharsets.UTF_8);

					if (addressBook.containsKey(sendTo)) {
						Account recieveMessageAccount = addressBook.get(sendTo);

						// The person's name
						recieveMessageAccount.sendMessage(name.getBytes());
						// name will need to be encrypted
						recieveMessageAccount.sendMessage(messageIn);

						System.out.printf("%s send message to %s: %s%n", name,
								sendTo,
								new String(messageIn, StandardCharsets.UTF_8));
					} else if (sendTo.equals(MessageType.NEWFRIEND.name())) {
						// decrypt inMsg to find person

						String recieveRequestName = new String(messageIn,
								StandardCharsets.UTF_8);

						if (addressBook.containsKey(recieveRequestName)) {
							// send to new friend request
							Account receiveRequestAccount = addressBook.get(recieveRequestName);
							receiveRequestAccount.sendMessage(
									MessageType.NEWFRIEND.name().getBytes());
							receiveRequestAccount.sendMessage(name.getBytes());
							receiveRequestAccount.sendMessage(
									account.getPublicKey().getEncoded());
							// give pub key

							// send requester information about new person
							// TODO wait for request to be accepted
							ex.sendMessage(
									MessageType.NEWFRIEND.name().getBytes());
							ex.sendMessage(recieveRequestName.getBytes());
							ex.sendMessage(receiveRequestAccount.getPublicKey().getEncoded());

						} else {
							// Person no exist, so communicate that
//							ex.sendMessage(MessageType.NO.name().getBytes());
						}
					} else {
						System.out.println("Invalid user/ command");
					}
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
		});
		listeningToCurrentConnections.start();
	}

	public static void main(String[] args) throws IOException {
		new Server();
	}
}
