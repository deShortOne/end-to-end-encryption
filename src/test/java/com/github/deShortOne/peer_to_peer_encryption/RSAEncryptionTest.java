package com.github.deShortOne.peer_to_peer_encryption;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RSAEncryptionTest {

	@Test
	public void testEncryption_OneUser() throws NoSuchAlgorithmException,
			FileNotFoundException, InvalidKeySpecException, IOException,
			InvalidKeyException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException {
		RSAEncryption p1 = new RSAEncryption();

		String msg = "WeatherIsMostlysUNNY";
		String cipher = p1.encrypt(msg);
		String msg2 = p1.decrypt(cipher);

		Assertions.assertEquals(msg, msg2);
	}
}
