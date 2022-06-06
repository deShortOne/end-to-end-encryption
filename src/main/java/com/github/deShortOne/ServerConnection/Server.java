package com.github.deShortOne.ServerConnection;

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
import com.github.deShortOne.Account.ServerAccount;
import com.github.deShortOne.end_to_end_encryption.Exchange;

public class Server {

	/**
	 * Name + connection of that user. Name should be replaced with contact.
	 */
	private HashMap<String, ServerAccount> addressBook = new HashMap<>();

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

					String name = new String(cm.recieveMessage(ex.recieveMessage()),
							StandardCharsets.UTF_8);
					PublicKey pubKey = null;
					try {
						pubKey = CryptMessage
								.createPublicKey(ex.recieveMessage());
					} catch (InvalidKeySpecException e) {
						e.printStackTrace();
					}

					ServerAccount acc = new ServerAccount(pubKey, ex);
					addressBook.put(name, acc);
					setupListening(name, acc);
					System.out.println("New connection from " + name + " "
							+ addressBook.size());
					System.out.println("Server done");

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
	private void setupListening(String name,
			ServerAccount account) {
		ServerListener sl = new ServerListener(name, account, addressBook, cm);
		Thread t1 = new Thread(sl);
		t1.start();
	}

	public static void main(String[] args) throws IOException {
		new Server();
	}
}
