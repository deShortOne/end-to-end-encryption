package com.github.deShortOne.peer_to_peer_encryption;

import java.security.PublicKey;

import com.baeldung.encryption.CryptMessage;

/**
 * Information about this person.
 * 
 * @author deShortOne
 *
 */
public class Account {

	private String name;
	private PublicKey pubKey;

	public Account(String name, PublicKey publicKey) {
		this.name = name;
		pubKey = publicKey;
	}

	public String getName() {
		return name;
	}

	public PublicKey getPublicKey() {
		return pubKey;
	}
	
	public byte[] encryptMessage(byte[] message) {
		return CryptMessage.createMessage(message, pubKey);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		return name.equals(((Account) o).getName());
	}
}
