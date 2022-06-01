package com.baeldung.encryption;

import java.io.ByteArrayOutputStream;
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
	 * @return RSA encrypted version of AES keys and AES encrypted version of
	 *         message
	 */
	public static byte[] createMessage(byte[] clearMessage, PublicKey pubKey) {
		try {
			SecretKey key = AESEncryption.generateKey(256);
			IvParameterSpec iv = AESEncryption.generateIv();

			StringBuilder sb = new StringBuilder();
			sb.append(Base64.getEncoder().encodeToString(key.getEncoded()));
			sb.append(Base64.getEncoder().encodeToString(iv.getIV()));

			byte[] base = RSAEncryption.encrypt(sb.toString().getBytes(),
					pubKey);

			byte[] encryptedMsg = AESEncryption.encrypt(algorithm, clearMessage,
					key, iv);

			// I understand that it's highly likely they are always the same
			// length
			// i.e. base length will never change and encryptedMsg length will
			// never change
			// but this is just to cover all the bases.
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			// will need to simplify.../ use proper methods
			if (base.length > 127) {
				outputStream.write(-1);

				String temp = Integer.toString(base.length);
				for (char c : temp.toCharArray()) {
					outputStream.write(c - '0');
				}
				outputStream.write(-1);
			} else {
				outputStream.write(base.length);
			}
			if (encryptedMsg.length > 127) {
				outputStream.write(-1);

				String temp = Integer.toString(encryptedMsg.length);
				for (char c : temp.toCharArray()) {
					outputStream.write(c - '0');
				}
				outputStream.write(-1);
			} else {
				outputStream.write(encryptedMsg.length);
			}

			outputStream.write(base);
			outputStream.write(encryptedMsg);

			return outputStream.toByteArray();
		} catch (NoSuchAlgorithmException | IOException | InvalidKeyException
				| NoSuchPaddingException | InvalidAlgorithmParameterException
				| IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public byte[] recieveMessage(byte[] cipherIn) {

		int n = 0;
		byte[] cipherBase;

		if (cipherIn[n] == -1) {
			n = 1;
			int num = 0;
			do {
				num *= 10;
				num += cipherIn[n];
			} while (cipherIn[++n] != -1);
			n += 1;

			cipherBase = new byte[num];
		} else {
			cipherBase = new byte[cipherIn[n++]];
		}

		byte[] cipherMessage;

		if (cipherIn[n] == -1) {
			n += 1;
			int num = 0;
			do {
				num *= 10;
				num += cipherIn[n];
			} while (cipherIn[++n] != -1);
			n += 1;

			cipherMessage = new byte[num];
		} else {
			cipherMessage = new byte[cipherIn[n++]];
		}

		for (int size = 0; size < cipherBase.length; size++, n++) {
			try {
				cipherBase[size] = cipherIn[n];
			} catch (IndexOutOfBoundsException e) {

				for (byte b : cipherIn) {
					System.out.println(b);
				}
				throw e;
			}
		}

		for (int size = 0; size < cipherMessage.length; size++, n++) {
			cipherMessage[size] = cipherIn[n];
		}

		if (n != cipherIn.length)
			throw new IllegalStateException("Incorrect message size");

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
}
