package com.baeldung.encryption;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptMessage {

	private RSAEncryption rsa;

	private static KeyFactory keyFactory;
	private static String algorithm = "AES/CBC/PKCS5Padding";

	public CryptMessage(RSAEncryption rsa) {
		this.rsa = rsa;

		try {
			keyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return rsa.getName();
	}

	public byte[] getPublicKey() {
		return rsa.getPublicKey().getEncoded();
	}

	public static PublicKey createPublicKey(byte[] publicKeyBytes)
			throws InvalidKeySpecException {
		if (keyFactory == null)
			return null;

		EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
		return keyFactory.generatePublic(publicKeySpec);
	}

	/**
	 * Returned byte[][] should be sent to reciever
	 * 
	 * @param clearMessage
	 * @param pubKey
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @return RSA encrypted version of AES keys, AES encrypted version of
	 *         message
	 */
	public static byte[][] createMessage(byte[] clearMessage,
			PublicKey pubKey) {

		try {
			SecretKey key = AESEncryption.generateKey(256);
			IvParameterSpec iv = AESEncryption.generateIv();

			StringBuilder sb = new StringBuilder();
			sb.append(Base64.getEncoder().encodeToString(key.getEncoded()));
			sb.append(Base64.getEncoder().encodeToString(iv.getIV()));

			byte[] base = RSAEncryption.encrypt(sb.toString(), pubKey);

			byte[] encryptedMsg = AESEncryption.encrypt(algorithm, clearMessage,
					key, iv);

			return new byte[][] { base, encryptedMsg };
		} catch (NoSuchAlgorithmException e) {
			System.err.println("AES Encryption algorithm incorrect");
			return null;
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public byte[] recieveMessage(byte[] cipherBase, byte[] cipherMessage) {

		try {
			String base = rsa.decrypt(cipherBase);

			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < 44; i++) {
				sb.append(base.charAt(i));
			}
			SecretKey key = new SecretKeySpec(
					Base64.getDecoder().decode(sb.toString()), 0, 32, "AES");

			sb.setLength(0);
			for (int i = 44; i < 68; i++) {
				sb.append(base.charAt(i));
			}
			IvParameterSpec iv = new IvParameterSpec(
					Base64.getDecoder().decode(sb.toString()));
			return AESEncryption.decrypt(algorithm, cipherMessage, key, iv);
		} catch (InvalidKeyException | NoSuchPaddingException
				| NoSuchAlgorithmException | InvalidAlgorithmParameterException
				| BadPaddingException | IllegalBlockSizeException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param base
	 * @param cipherMessage
	 * @param priKey
	 * @return
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidAlgorithmParameterException
	 */
	public static byte[] recieveMessage(byte[] cipherBase, byte[] cipherMessage,
			PrivateKey priKey) throws InvalidKeyException {

		String base;
		try {
			base = RSAEncryption.decrypt(cipherBase, priKey);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
			return "_RSAEncryption_fault_".getBytes();
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 44; i++) {
			sb.append(base.charAt(i));
		}
		SecretKey key = new SecretKeySpec(
				Base64.getDecoder().decode(sb.toString()), 0, 32, "AES");

		sb.setLength(0);
		for (int i = 44; i < 68; i++) {
			sb.append(base.charAt(i));
		}
		IvParameterSpec iv = new IvParameterSpec(
				Base64.getDecoder().decode(sb.toString()));

		try {
			return AESEncryption.decrypt(algorithm, cipherMessage, key, iv);
		} catch (NoSuchPaddingException | NoSuchAlgorithmException
				| InvalidAlgorithmParameterException | BadPaddingException
				| IllegalBlockSizeException e) {
			e.printStackTrace();
			return "_AESEncryption_fault_".getBytes();
		}
	}

	public static byte[][] sendFile(File file, PublicKey pubKey)
			throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, InvalidAlgorithmParameterException,
			IOException {

		SecretKey key = AESEncryption.generateKey(256);
		IvParameterSpec iv = AESEncryption.generateIv();

		StringBuilder sb = new StringBuilder();
		sb.append(Base64.getEncoder().encodeToString(key.getEncoded()));
		sb.append(Base64.getEncoder().encodeToString(iv.getIV()));

		byte[] base = RSAEncryption.encrypt(sb.toString(), pubKey);

		byte[] encryptedFile = AESEncryption.encryptFile(algorithm, key, iv,
				file);

		return new byte[][] { base, encryptedFile };
	}

	/**
	 * Currently deprecated as is incorrect
	 */
	@Deprecated
	public static String recieveFile(byte[] cipherBase, byte[] cipherFile,
			PrivateKey priKey)
			throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, InvalidAlgorithmParameterException {

		String base = RSAEncryption.decrypt(cipherBase, priKey);

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 44; i++) {
			sb.append(base.charAt(i));
		}
		SecretKey key = new SecretKeySpec(
				Base64.getDecoder().decode(sb.toString()), 0, 32, "AES");

		sb.setLength(0);
		for (int i = 44; i < 68; i++) {
			sb.append(base.charAt(i));
		}
		IvParameterSpec iv = new IvParameterSpec(
				Base64.getDecoder().decode(sb.toString()));

		String clearMsg = AESEncryption.decrypt(algorithm, cipherFile, key, iv);
		return null;
	}
}
