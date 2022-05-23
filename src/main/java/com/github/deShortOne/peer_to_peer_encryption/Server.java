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
	private HashMap<Account, Exchange> addressBook = new HashMap<>();

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

					Account acc = new Account(name, pubKey);
					addressBook.put(acc, ex);
					setupListening(acc, ex);
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
	private void setupListening(Account acc, Exchange ex) {
		listeningToCurrentConnections = new Thread(() -> {
			while (true)
				try {
					byte[] msgType = ex.recieveMessage();
					byte[] inMsg = ex.recieveMessage();
					// Encrypted with recipients public key if message for
					// reciptent
					// Encrypted with server's public key if message for server,
					// cause duh

					// Decode msgType using Server's private key
					String sendTo = new String(msgType, StandardCharsets.UTF_8);

					// need to find way to override containsKey so that it will
					// find name instead of comparing objects
					Account toFind = new Account(sendTo, null);
					if (addressBook.containsKey(toFind)) {
						// The person's name
						addressBook.get(toFind)
								.sendMessage(acc.getName().getBytes());
						// name will need to be encrypted
						addressBook.get(toFind).sendMessage(inMsg);

						System.out.printf("%s send message to %s: %s%n",
								acc.getName(), sendTo,
								new String(inMsg, StandardCharsets.UTF_8));
					} else if (sendTo.equals(MessageType.NEWFRIEND.name())) {
						// decrypt inMsg to find person

						String nameOther = new String(inMsg,
								StandardCharsets.UTF_8);

						Account otherPerson = new Account(nameOther, null);
						if (addressBook.containsKey(otherPerson)) {
							Exchange exOther = addressBook.get(otherPerson);
							exOther.sendMessage(
									MessageType.NEWFRIEND.name().getBytes());
							exOther.sendMessage(acc.getName().getBytes());
							exOther.sendMessage(
									acc.getPublicKey().getEncoded());
							// give pub key

							ex.sendMessage(
									MessageType.NEWFRIEND.name().getBytes());
							ex.sendMessage(otherPerson.getName().getBytes());

							// Very poor implementation but will have to think
							// of another way another time
							Account aOther = null;
							for (Account a : addressBook.keySet()) {
								if (a.equals(otherPerson)) {
									aOther = a;
								}
							}
							ex.sendMessage(aOther.getPublicKey().getEncoded());

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
		});
		listeningToCurrentConnections.start();
	}

	public static void main(String[] args) throws IOException {
		new Server();
	}
}
