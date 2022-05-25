package com.github.deShortOne.peer_to_peer_encryption;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.baeldung.encryption.RSAEncryption;

public class RSAEncryptionTest {

	@Test
	public void testEncryption_OneUser()
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeySpecException, IOException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		RSAEncryption p1 = new RSAEncryption("oneUser", "", true);

		String msg = "WeatherIsMostlysUNNY";
		byte[] cipher = RSAEncryption.encrypt(msg.getBytes(), p1.getPublicKey());
		String msg2 = p1.decrypt(cipher);

		Assertions.assertEquals(msg, msg2);
	}

	@Test
	public void testStoringPublicPrivateKey()
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeySpecException, IOException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		RSAEncryption p1 = new RSAEncryption("storingPubPri", "Test", true);

		String msg = "WeatherIsMostlysUNNY";
		byte[] cipher = RSAEncryption.encrypt(msg.getBytes(), p1.getPublicKey());

		RSAEncryption p2 = new RSAEncryption("storingPubPri", "Test", false);
		String msg2 = p2.decrypt(cipher);

		Assertions.assertEquals(msg, msg2);

		Assertions.assertTrue(
				new File("public_keys\\storingPubPri.pubkey").isFile());
		Assertions.assertTrue(
				new File("public_keys\\storingPubPri.prikey").isFile());
	}

	@Test
	public void testEncryption_ImitateMultipleUsers()
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeySpecException, IOException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		RSAEncryption p1 = new RSAEncryption("imitateMulti1", "asdf12", true);
		RSAEncryption p2 = new RSAEncryption("imitateMulti2", "asdf21", true);

		String msg = "WeatherIsMostlysUNNY";

		byte[] cipher = RSAEncryption.encrypt(msg.getBytes(), p2.getPublicKey());
		String msg2 = p2.decrypt(cipher);

		Assertions.assertEquals(msg, msg2);
		
		msg = "funnystorythat ikr?";
		byte[] cipher2 = RSAEncryption.encrypt(msg.getBytes(), p1.getPublicKey());
		msg2 = p1.decrypt(cipher2);
		
		Assertions.assertEquals(msg, msg2);
	}

	@Test
	public void signAndVerify()
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeySpecException, IOException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, SignatureException {
		RSAEncryption p1 = new RSAEncryption("signAndVerify1", "asdf", true);
		RSAEncryption p2 = new RSAEncryption("signAndVerify2", "asdf", true);

		byte[] bytes = new byte[20];
		SecureRandom.getInstanceStrong().nextBytes(bytes);
		
		byte[] send = p1.signMessage(bytes, p2.getPublicKey());

		Assertions.assertTrue(p2.verifyMessage(send, p1.getPublicKey(), bytes));
		
		// If exchanging public keys, send public key, sign and verify it's the owners
		// Should already store previous public keys and double check name
		
		// If not already exchanged then hard coded public key to server?
	}
}
