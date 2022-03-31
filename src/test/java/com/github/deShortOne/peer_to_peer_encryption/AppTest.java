package com.github.deShortOne.peer_to_peer_encryption;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AppTest {

	@Test
	void givenString_whenEncrypt_thenSuccess() throws NoSuchAlgorithmException,
			IllegalBlockSizeException, InvalidKeyException, BadPaddingException,
			InvalidAlgorithmParameterException, NoSuchPaddingException {

		String input = "baeldung";
		SecretKey key = AESEncryption.generateKey(128);
		IvParameterSpec ivParameterSpec = AESEncryption.generateIv();
		String algorithm = "AES/CBC/PKCS5Padding";
		String cipherText = AESEncryption.encrypt(algorithm, input, key,
				ivParameterSpec);
		String plainText = AESEncryption.decrypt(algorithm, cipherText, key,
				ivParameterSpec);
		Assertions.assertEquals(input, plainText);
	}
}