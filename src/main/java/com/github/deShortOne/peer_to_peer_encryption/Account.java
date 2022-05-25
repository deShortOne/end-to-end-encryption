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

	public void sendMessage(byte[] msg) throws IOException {
		ex.sendMessage(msg);
	}

	public byte[] encryptMessage(byte[] message) {
		return CryptMessage.createMessage(message, pubKey);
	}
	
	public void setExchange(Exchange exchange) {
		this.ex = exchange;
	}
}
