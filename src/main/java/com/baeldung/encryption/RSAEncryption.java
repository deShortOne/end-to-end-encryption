package com.baeldung.encryption;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
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

	public static String publicKeyFileLoc = "public_keys\\";

	private PublicKey publicKey;

	private PrivateKey privateKey;

	private String username;

	private String password;

	private static Cipher cipher;
	private static KeyFactory keyFactory;

	/*
	 * Should throw all to simplify matters a little
	 */
	public RSAEncryption(String username, String password, boolean newKeys)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			IOException, InvalidKeySpecException {
		this.username = username;
		this.password = password;

		RSAEncryption.cipher = Cipher.getInstance("RSA");
		RSAEncryption.keyFactory = KeyFactory.getInstance("RSA");

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
	public void generateKeys() throws NoSuchAlgorithmException, IOException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(2048);
		KeyPair pair = generator.generateKeyPair();
		privateKey = pair.getPrivate();
		publicKey = pair.getPublic();

		try (FileOutputStream fos = new FileOutputStream(
				publicKeyFileLoc + username + ".pubkey")) {
			fos.write(publicKey.getEncoded());
		} catch (FileNotFoundException e) {
			File tmpFolder = new File(publicKeyFileLoc);
			if (!tmpFolder.exists()) {
				tmpFolder.mkdir();
			}

			File tmp = new File(publicKeyFileLoc + username + ".pubkey");
			tmp.createNewFile();

			FileOutputStream fos = new FileOutputStream(
					publicKeyFileLoc + username + ".pubkey");

			fos.write(publicKey.getEncoded());
			fos.close();
		}

		// FIXME be encrypted with users password
		try (FileOutputStream fos = new FileOutputStream(
				publicKeyFileLoc + username + ".prikey")) {
			fos.write(privateKey.getEncoded());
		} catch (FileNotFoundException e) {
			File tmp = new File(publicKeyFileLoc + username + ".prikey");
			tmp.createNewFile();

			FileOutputStream fos = new FileOutputStream(
					publicKeyFileLoc + username + ".prikey");

			fos.write(privateKey.getEncoded());
			fos.close();
		}
	}

	/**
	 * Should be used for debug only.
	 * 
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	@Deprecated
	public static PublicKey getCommonKey() throws IOException,
			NoSuchAlgorithmException, InvalidKeySpecException {
		File publicKeyFile = new File(publicKeyFileLoc + ".pubkey");
		byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());

		EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
		return keyFactory.generatePublic(publicKeySpec);
	}

	public static PublicKey createPublicKey(byte[] publicKeyBytes)
			throws InvalidKeySpecException {

		EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
		return keyFactory.generatePublic(publicKeySpec);
	}

	/**
	 * Get personal keys from file.
	 * 
	 * @throws IOException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 */
	public void getKeys() throws IOException, InvalidKeySpecException {

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

	/**
	 * Get public key from file, for contacts.
	 * 
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static PublicKey getPublicKeyFromFile(String name)
			throws IOException, InvalidKeySpecException {
		return getPublicKeyFromFile(
				new File(publicKeyFileLoc + name + ".pubkey").toPath());
	}

	public static PublicKey getPublicKeyFromFile(Path path)
			throws IOException, InvalidKeySpecException {
		byte[] publicKeyBytes = Files.readAllBytes(path);

		EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
		// publicKey = keyFactory.generatePublic(publicKeySpec);
		return keyFactory.generatePublic(publicKeySpec);
	}

	public static byte[] encrypt(byte[] clearText, Key key)
			throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException {
		cipher.init(Cipher.ENCRYPT_MODE, key);

		byte[] secretMessageBytes = clearText;
		byte[] encryptedMessageBytes = cipher.doFinal(secretMessageBytes);
		return encryptedMessageBytes;
	}

	public String decrypt(byte[] cipherText) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		return decrypt(cipherText, privateKey);
	}

	public static String decrypt(byte[] cipherText, Key key)
			throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException {

		cipher.init(Cipher.DECRYPT_MODE, key);

		byte[] decryptedMessageBytes = cipher.doFinal(cipherText);
		String decryptedMessage = new String(decryptedMessageBytes,
				StandardCharsets.UTF_8);
		return decryptedMessage;
	}

	public byte[] signMessage(String msg, PublicKey key)
			throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException, SignatureException {
		
		return signMessage(msg.getBytes(StandardCharsets.UTF_8), key);
	}

	public byte[] signMessage(byte[] clearText, PublicKey key)
			throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException, SignatureException {

		Signature sig = Signature.getInstance("SHA1WithRSA");
		sig.initSign(privateKey);
		sig.update(clearText);
		byte[] signatureBytes = sig.sign();

		sig.initVerify(publicKey);
		sig.update(clearText);

		return signatureBytes;
	}

	public boolean verifyMessage(byte[] clearText, PublicKey key, byte[] msg)
			throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, SignatureException, NoSuchAlgorithmException {
		Signature sig = Signature.getInstance("SHA1WithRSA");
		sig.initVerify(key);
		sig.update(msg);

		return sig.verify(clearText);
	}

	/**
	 * 
	 * FIXME: Should this ever return public key or only byte[]
	 * 
	 * @return
	 */
	public PublicKey getPublicKey() {
		return publicKey;
	}

	public String getName() {
		return username;
	}

}
