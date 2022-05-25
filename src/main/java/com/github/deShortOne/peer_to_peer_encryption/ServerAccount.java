package com.github.deShortOne.peer_to_peer_encryption;

import java.io.IOException;
import java.security.PublicKey;

/**
 * Information about this person.
 * 
 * @author deShortOne
 *
 */
public class ServerAccount extends Account {

	/**
	 * Server connection to client or client connection to Server via exchange.
	 */
	private Exchange ex;

	/**
	 * Connection to server or client.
	 * 
	 * @param publicKey public key of recipient
	 * @param exchange  exchange connection to server or client
	 */
	public ServerAccount(PublicKey publicKey, Exchange exchange) {
		super(publicKey);
		this.ex = exchange;
	}

	/**
	 * Returns exchange connection.
	 * 
	 * @return
	 */
	public Exchange getExchange() {
		return ex;
	}

	/**
	 * Sends message to recipitent or client.
	 * TODO encrypt sender
	 * 
	 * @param sender
	 * @param message
	 * @throws IOException 
	 */
	public void sendMessage(String sender, byte[] message) throws IOException {
		ex.sendMessage(sender.getBytes());
		ex.sendMessage(message);
	}

	/**
	 * Sends friend request to server.
	 * 
	 * @param name
	 * @throws IOException 
	 */
	public void sendFriendRequest(String name) throws IOException {
		ex.sendMessage(MessageType.NEWFRIEND.name().getBytes());
		ex.sendMessage(name.getBytes());
	}

	/**
	 * Sends friend request to client.
	 * 
	 * @param name   name of person requesting or accepting the friend request
	 * @param pubKey public key if person accepts friend request or being
	 *               requested, null otherwise
	 * @throws IOException 
	 */
	public void friendRequest(String name, byte[] pubKey) throws IOException {
		ex.sendMessage(MessageType.NEWFRIEND.name().getBytes());
		ex.sendMessage(name.getBytes());
		ex.sendMessage(pubKey);
	}

	/**
	 * Sets the exchange connection.
	 * 
	 * @param exchange
	 */
	public void setExchange(Exchange exchange) {
		this.ex = exchange;
	}
	
	public byte[] recieveMessage() throws IOException {
		return ex.recieveMessage();
	}
}
