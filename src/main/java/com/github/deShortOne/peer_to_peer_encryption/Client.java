package com.github.deShortOne.peer_to_peer_encryption;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Client extends Exchange {

	/**
	 * Name of user of the client.
	 */
	private String name;

	/**
	 * Thread that listens to server.
	 */
	private Thread serverListener;

	/**
	 * Name + conversation page linked to it. Name should be class called Object
	 * which holds other information like public key
	 */
	private HashMap<String, ConversationPage> messages = new HashMap<>();

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
		establishConnectionToServer();
		listenToServer();
	}

	/**
	 * Send message.
	 * 
	 * @param msg
	 * @throws IOException
	 */
	public void sendMessage(String recipetent, String msg) throws IOException {
		messages.get(recipetent).addText(msg);
		// TODO Encrypt it!
		super.sendMessage(recipetent.getBytes());
		super.sendMessage(msg.getBytes());
	}

	public ConversationPage getMessages(String name) {
		return messages.get(name);
	}

	/**
	 * Makes friend request to server and returns true, returns false if server
	 * cannot find reciptent.
	 * 
	 * @param recipitent name of person to add
	 * @return boolean if that name exists in server's database [currently doesn't work]
	 * @throws IOException
	 */
	public boolean addFriend(String recipitent) throws IOException {
		super.sendMessage(MessageType.NEWFRIEND.name().getBytes());
		super.sendMessage(recipitent.getBytes());

		messages.put(recipitent, new ConversationPage());
		messages.get(recipitent).addText(recipitent
				+ " has recieved your friend request\nWait for them to accept!");

		return true;

		// Cannot recieve cause there's a thread that's already listening... TODO
//		System.out.println("Waiting to recieve"); // Not recieved???
//		byte[] exist = super.recieveMessage();
//		System.out.println("Recieved");
//		
//		// decrypt
//		String str = new String(exist, StandardCharsets.UTF_8);
//		
//		return str.equals(MessageType.YES.name());
	}

	/**
	 * Stops listening to server.
	 */
	public void exit() {
		serverListener.interrupt();
		// TODO tells server that connection is FIN
	}

	/**
	 * Creates connection to server.
	 * 
	 * @throws IOException
	 */
	private void establishConnectionToServer() throws IOException {
		Socket socket = new Socket(InetAddress.getLoopbackAddress(), 8080);
		super.setSocket(socket);

		super.sendMessage(name.getBytes());
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

					if (Server.autoAddName && !messages.containsKey(sender)) {
						// TODO add new person into MessageWindow
						messages.put(sender, new ConversationPage());
						System.out.println("New person!");
						mw.addContact(sender);
					}

					if (messages.containsKey(sender)) {
						messages.get(sender).addText(msg);
					} else if (sender.equals(MessageType.NEWFRIEND.name())) {
						messages.put(msg, new ConversationPage());
						messages.get(msg).addText(msg
								+ " wants to be your friend!\nReply to accept!");
						System.out.println("New person!");
						mw.addContact(msg);
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
