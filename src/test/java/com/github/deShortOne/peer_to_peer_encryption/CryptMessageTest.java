package com.github.deShortOne.peer_to_peer_encryption;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

import javax.crypto.NoSuchPaddingException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.baeldung.encryption.CryptMessage;
import com.baeldung.encryption.RSAEncryption;

public class CryptMessageTest {

	HashMap<String, PublicKey> friendsList = new HashMap<>();

	@Test
	public void sendMessage_onePerson() throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeySpecException, IOException {
		RSAEncryption friend1 = new RSAEncryption("", "", true);
		CryptMessage cm = new CryptMessage(friend1);

		onePerson("HI", cm, friend1);

		String msg = "?? 24 091!'; this message needs to be ridicouslylylyl long, how much longer do you want me to be??";
		onePerson(msg + msg + msg + msg + msg + msg + msg + msg + msg, cm,
				friend1);

	}

	public void onePerson(String msg, CryptMessage cm, RSAEncryption friend1) {
		byte[] sentMessage = CryptMessage.createMessage(msg.getBytes(),
				friend1.getPublicKey());
		Assertions.assertNotNull(sentMessage);

		byte[] asdf = cm.recieveMessage(sentMessage);
		Assertions.assertNotNull(asdf);

		String recievedMessage = new String(asdf, StandardCharsets.UTF_8);
		Assertions.assertEquals(msg, recievedMessage);
	}
}
