package com.github.deShortOne.peer_to_peer_encryption;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
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
	public void testEncryption_OneUser()
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeySpecException, IOException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		RSAEncryption p1 = new RSAEncryption("oneUser", "", true);

		String msg = "WeatherIsMostlysUNNY";
		String cipher = p1.encrypt(msg);
		String msg2 = p1.decrypt(cipher);

		Assertions.assertEquals(msg, msg2);
	}

	@Test
	public void testEncryption_OneUser_File()
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeySpecException, IOException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		RSAEncryption p1 = new RSAEncryption("OneUserFile", "", true);

		String fileLocation = "dummy_files/";

		File inputFile = new File(fileLocation + "dummy.txt");
		File encryptedFile = new File(fileLocation + "document.encrypted");
		File decryptedFile = new File(fileLocation + "document.decrypted");

		p1.encryptFile(inputFile, encryptedFile);
		p1.decryptFile(encryptedFile, decryptedFile);

		assertThat(inputFile).hasSameTextualContentAs(decryptedFile);
	}

	@Test
	public void testStoringPublicPrivateKey()
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeySpecException, IOException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		RSAEncryption p1 = new RSAEncryption("storingPubPri", "Test", true);

		String msg = "WeatherIsMostlysUNNY";
		String cipher = p1.encrypt(msg);

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
		RSAEncryption p1 = new RSAEncryption("imitateMulti", "asdf", true);

		String msg = "WeatherIsMostlysUNNY";

		String cipher = p1.encrypt(msg);
		String msg2 = p1.decrypt(cipher);

		Assertions.assertEquals(msg, msg2);
	}
}
