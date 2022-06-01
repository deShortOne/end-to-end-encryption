package com.github.deShortOne.Account;

import java.io.IOException;
import java.security.PublicKey;

import com.github.deShortOne.end_to_end_encryption.Exchange;
import com.github.deShortOne.end_to_end_encryption.MessageType;

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
	 * Sends message to recipient or client.
	 * TODO encrypt sender
	 * 
	 * @param sender
	 * @param message
	 * @throws IOException 
	 */
	public void sendMessage(String cSender, byte[] eMsg) throws IOException {
		ex.sendMessage(cSender.getBytes());
		ex.sendMessage(eMsg);
	}

	/**
	 * Sends friend request to server.
	 * 
	 * @param name
	 * @throws IOException 
	 */
	public void sendFriendRequest(String cName) throws IOException {
		ex.sendMessage(MessageType.NEWFRIEND.name().getBytes());
		ex.sendMessage(cName.getBytes());
	}

	/**
	 * Sends friend request to client.
	 * 
	 * @param name   name of person requesting or accepting the friend request
	 * @param pubKey public key if person accepts friend request or being
	 *               requested, null otherwise
	 * @throws IOException 
	 */
	public void friendRequest(byte[] eName, byte[] ePubKey) throws IOException {
		ex.sendMessage(MessageType.NEWFRIEND.name().getBytes());
		ex.sendMessage(eName);
		ex.sendMessage(ePubKey);
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
