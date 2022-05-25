package com.github.deShortOne.peer_to_peer_encryption;

import java.security.PublicKey;

import com.baeldung.encryption.CryptMessage;

/**
 * 
 * 
 * @author deShortOne
 *
 */
public abstract class Account {

	/**
	 * Other person's public key.
	 */
	private PublicKey pubKey;

	/**
	 * Constructs account class for other person to be able to send messages to.
	 * 
	 * @param publicKey	other person's public key
	 * @param serverAccount	connection to person via server
	 */
	public Account(PublicKey publicKey) {
		pubKey = publicKey;
	}

	public PublicKey getPublicKey() {
		return pubKey;
	}

	public byte[] encryptMessage(byte[] message) {
		return CryptMessage.createMessage(message, pubKey);
	}
}
