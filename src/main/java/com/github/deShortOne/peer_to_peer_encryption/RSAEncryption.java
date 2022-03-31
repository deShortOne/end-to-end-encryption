package com.github.deShortOne.peer_to_peer_encryption;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

// https://www.baeldung.com/java-rsa
public class RSAEncryption {

	static String publicKeyFileLoc = "public_keys\\";

	private PublicKey publicKey;

	private PrivateKey privateKey;

	public RSAEncryption() throws NoSuchAlgorithmException,
			FileNotFoundException, InvalidKeySpecException, IOException {
		generateKeys();
	}

	/**
	 * Generate public prinvate keys and store in file.
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InvalidKeySpecException
	 */
	public void generateKeys() throws NoSuchAlgorithmException,
			FileNotFoundException, IOException, InvalidKeySpecException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(2048);
		KeyPair pair = generator.generateKeyPair();
		privateKey = pair.getPrivate();
		publicKey = pair.getPublic();

		try (FileOutputStream fos = new FileOutputStream(
				publicKeyFileLoc + "public.key")) {
			fos.write(publicKey.getEncoded());
		}
	}

	public void getPublicKey() throws IOException, NoSuchAlgorithmException,
			InvalidKeySpecException {
		File publicKeyFile = new File(publicKeyFileLoc + "public.key");
		byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
		keyFactory.generatePublic(publicKeySpec);
	}

	public String encrypt(String clearText) throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException {
		Cipher encryptCipher = Cipher.getInstance("RSA");
		encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

		byte[] secretMessageBytes = clearText.getBytes(StandardCharsets.UTF_8);
		byte[] encryptedMessageBytes = encryptCipher
				.doFinal(secretMessageBytes);

		String encodedMessage = Base64.getEncoder()
				.encodeToString(encryptedMessageBytes);
		return encodedMessage;
	}

	public String decrypt(String cipherText) throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException {
		Cipher decryptCipher = Cipher.getInstance("RSA");
		decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);

		byte[] secretMessageBytes = cipherText.getBytes(StandardCharsets.UTF_8);
		byte[] decryptedMessageBytes = decryptCipher
				.doFinal(Base64.getDecoder().decode(secretMessageBytes));
		String decryptedMessage = new String(decryptedMessageBytes,
				StandardCharsets.UTF_8);
		return decryptedMessage;
	}

	/**
	 * Should only be used with small files!
	 * 
	 * @param input
	 * @param output
	 * @throws IOException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public void encryptFile(File input, File output)
			throws IOException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		byte[] fileBytes = Files.readAllBytes(input.toPath());

		Cipher encryptCipher = Cipher.getInstance("RSA");
		encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] encryptedFileBytes = encryptCipher.doFinal(fileBytes);

		try (FileOutputStream stream = new FileOutputStream(output)) {
			stream.write(encryptedFileBytes);
		}
	}

	public void decryptFile(File input, File output)
			throws IOException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		byte[] encryptedFileBytes = Files.readAllBytes(input.toPath());
		Cipher decryptCipher = Cipher.getInstance("RSA");
		
		decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] decryptedFileBytes = decryptCipher.doFinal(encryptedFileBytes);
		
		try (FileOutputStream stream = new FileOutputStream(output)) {
			stream.write(decryptedFileBytes);
		}
	}
}
