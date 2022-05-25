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

	private ServerAccount serverConnection;

	private CryptMessage cm;

	/**
	 * Name + Client Account. Client Account holds public key, conversation page
	 * and exchange connection.
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
		// recievingPerson.addMessage(msg);
		recievingPerson.sendMessage(msg);
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
		serverConnection.sendFriendRequest(recipitent);
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

		serverConnection.sendMessage(name, cm.getPublicKey());
		System.out.println("Client done");
	}

	/**
	 * Listens to server.
	 */
	private void listenToServer() {
		ClientListener cl = new ClientListener(serverConnection, messages, mw);
		serverListener = new Thread(cl);
		serverListener.start();
	}
}
