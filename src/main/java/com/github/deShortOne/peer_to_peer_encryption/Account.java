package com.github.deShortOne.peer_to_peer_encryption;

import java.io.IOException;
import java.security.PublicKey;

import com.baeldung.encryption.CryptMessage;

/**
 * Information about this person.
 * 
 * @author deShortOne
 *
 */
public abstract class Account {

	private PublicKey pubKey;
	private Exchange ex;

	public Account(PublicKey publicKey, Exchange exchange) {
		pubKey = publicKey;
		ex = exchange;
	}

	public PublicKey getPublicKey() {
		return pubKey;
	}

	public Exchange getExchange() {
		return ex;
	}

	// TODO encrypt
	public void sendMessage(byte[] message) throws IOException {
		ex.sendMessage(message);
	}

	public byte[] encryptMessage(byte[] message) {
		return CryptMessage.createMessage(message, pubKey);
	}
	
	public void setExchange(Exchange exchange) {
		this.ex = exchange;
	}
	
	public byte[] recieveMessage() throws IOException {
		return ex.recieveMessage();
	}
}
