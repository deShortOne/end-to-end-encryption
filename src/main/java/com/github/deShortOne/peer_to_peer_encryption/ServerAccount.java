package com.github.deShortOne.peer_to_peer_encryption;

import java.security.PublicKey;

import com.baeldung.encryption.CryptMessage;

/**
 * Information about this person.
 * 
 * @author deShortOne
 *
 */
public class ServerAccount extends Account {

	public ServerAccount(PublicKey publicKey, Exchange ex) {
		super(publicKey, ex);
	}
}
