package com.github.deShortOne.peer_to_peer_encryption;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
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

	private String username;

	private String password;

	/*
	 * Should throw all to simplify matters a little
	 */
	public RSAEncryption(String username, String password, boolean newKeys)
			throws NoSuchAlgorithmException, FileNotFoundException,
			InvalidKeySpecException, IOException, InvalidKeyException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException {
		this.username = username;
		this.password = password;
		if (newKeys)
			generateKeys();
		else {
			getKeys();
		}
	}

	/**
	 * Generate public private keys and store in file.
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
				publicKeyFileLoc + username + ".pubkey")) {
			fos.write(publicKey.getEncoded());
		}

		// FIXME be encrypted with users password
		try (FileOutputStream fos = new FileOutputStream(
				publicKeyFileLoc + username + ".prikey")) {
			fos.write(privateKey.getEncoded());
		}
	}

	/**
	 * 
	 * FIXME: private key needs to be encrypted somehow
	 * 
	 * @return
	 */
	public String[] getPublicKeys() {
		String[] out = new String[2];

		return out;
	}

	public static PublicKey getCommonKey() throws IOException,
			NoSuchAlgorithmException, InvalidKeySpecException {
		File publicKeyFile = new File(publicKeyFileLoc + "public.key");
		byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
		return keyFactory.generatePublic(publicKeySpec);
	}

	public static PublicKey createPublicKey(byte[] publicKeyBytes)
			throws InvalidKeySpecException {
		KeyFactory keyFactory;
		try {
			keyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}

		EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
		return keyFactory.generatePublic(publicKeySpec);
	}

	public void getKeys() throws IOException, InvalidKeySpecException,
			NoSuchAlgorithmException {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");

		File publicKeyFile = new File(publicKeyFileLoc + username + ".pubkey");
		byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());

		EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
		publicKey = keyFactory.generatePublic(publicKeySpec);

		File privateKeyFile = new File(publicKeyFileLoc + username + ".prikey");
		byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());

		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
				privateKeyBytes);
		privateKey = keyFactory.generatePrivate(privateKeySpec);
	}

	public void getPublicKeyFromFile() throws IOException,
			NoSuchAlgorithmException, InvalidKeySpecException {
		File publicKeyFile = new File(publicKeyFileLoc + "public.key");
		byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
		publicKey = keyFactory.generatePublic(publicKeySpec);
	}

	public String encrypt(String clearText) throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException {
		return Base64.getEncoder()
				.encodeToString(encrypt(clearText, publicKey));
	}

	public static byte[] encrypt(String clearText, PublicKey key)
			throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException {
		Cipher encryptCipher = Cipher.getInstance("RSA");
		encryptCipher.init(Cipher.ENCRYPT_MODE, key);

		byte[] secretMessageBytes = clearText.getBytes(StandardCharsets.UTF_8);
		byte[] encryptedMessageBytes = encryptCipher
				.doFinal(secretMessageBytes);
		return encryptedMessageBytes;
	}

	public String decrypt(String cipherText) throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException {

		return decrypt(
				Base64.getDecoder()
						.decode(cipherText.getBytes(StandardCharsets.UTF_8)),
				privateKey);
	}

	public static String decrypt(byte[] cipher, PrivateKey key)
			throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException {
		Cipher decryptCipher = Cipher.getInstance("RSA");
		decryptCipher.init(Cipher.DECRYPT_MODE, key);

		byte[] decryptedMessageBytes = decryptCipher.doFinal(cipher);
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

	/**
	 * DEBUG. Shouldn't normally call a method that isn't usually called in the
	 * normal use of the application
	 * 
	 * FIXME: Should this ever return public key or only byte[]
	 * 
	 * @return
	 */
	public PublicKey getPublicKey() {
		return publicKey;
	}

	@Deprecated
	public PrivateKey getPrivateKey() {
		return privateKey;
	}
}
