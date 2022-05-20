package com.github.deShortOne.peer_to_peer_encryption;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.github.deShortOne.encryption.AESEncryption;

import static org.assertj.core.api.Assertions.assertThat;

public class AESEncryptionTest {

	static String fileLocation = "dummy_files\\";

	@Test
	void givenString_whenEncrypt_thenSuccess() throws NoSuchAlgorithmException,
			IllegalBlockSizeException, InvalidKeyException, BadPaddingException,
			InvalidAlgorithmParameterException, NoSuchPaddingException {

		String input = "baeldung";
		SecretKey key = AESEncryption.generateKey(128);
		IvParameterSpec ivParameterSpec = AESEncryption.generateIv();
		String algorithm = "AES/CBC/PKCS5Padding";
		byte[] cipherText = AESEncryption.encrypt(algorithm, input, key,
				ivParameterSpec);
		String plainText = AESEncryption.decrypt(algorithm, cipherText, key,
				ivParameterSpec);
		Assertions.assertEquals(input, plainText);
	}

	@Test
	void givenFile_whenEncrypt_thenSuccess()
			throws NoSuchAlgorithmException, IOException,
			IllegalBlockSizeException, InvalidKeyException, BadPaddingException,
			InvalidAlgorithmParameterException, NoSuchPaddingException {

		SecretKey key = AESEncryption.generateKey(128);
		String algorithm = "AES/CBC/PKCS5Padding";
		IvParameterSpec ivParameterSpec = AESEncryption.generateIv();
		File inputFile = new File(fileLocation + "dummy.txt");
		File encryptedFile = new File(fileLocation + "document.encrypted");
		File decryptedFile = new File(fileLocation + "document.decrypted");
		AESEncryption.encryptFile(algorithm, key, ivParameterSpec, inputFile,
				encryptedFile);
		AESEncryption.decryptFile(algorithm, key, ivParameterSpec,
				encryptedFile, decryptedFile);
		assertThat(inputFile).hasSameTextualContentAs(decryptedFile);
	}

	@Test
	void givenPassword_whenEncrypt_thenSuccess()
			throws InvalidKeySpecException, NoSuchAlgorithmException,
			IllegalBlockSizeException, InvalidKeyException, BadPaddingException,
			InvalidAlgorithmParameterException, NoSuchPaddingException {

		String algorithm = "AES/CBC/PKCS5Padding";
		String plainText = "www.baeldung.com";
		String password = "baeldung";
		String salt = "12345678";
		IvParameterSpec ivParameterSpec = AESEncryption.generateIv();
		SecretKey key = AESEncryption.getKeyFromPassword(password, salt);
		byte[] cipherText = AESEncryption.encrypt(algorithm, plainText, key,
				ivParameterSpec);
		String decryptedCipherText = AESEncryption.decrypt(algorithm,
				cipherText, key, ivParameterSpec);
		Assertions.assertEquals(plainText, decryptedCipherText);

		// Should be calling below function
		// AESEncryotion.encryptPasswordBased(plainText, key, ivParameterSpec);
	}
}