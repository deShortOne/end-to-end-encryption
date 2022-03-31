package com.github.deShortOne.peer_to_peer_encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Mostly strictly ripped off
 * https://www.baeldung.com/java-aes-encryption-decryption
 */
public class AESEncryption {

	/**
	 * https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#SecretKeyFactory
	 */
	private static String secrectKeyAlgorithm = "PBKDF2WithHmacSHA256";

	public static void main() throws NoSuchAlgorithmException {
		System.out.println("Hello World!");
	}

	/**
	 * String encryption.
	 * @param algorithm
	 * @param input
	 * @param key
	 * @param iv
	 * @return
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidAlgorithmParameterException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public static String encrypt(String algorithm, String input, SecretKey key,
			IvParameterSpec iv)
			throws NoSuchPaddingException, NoSuchAlgorithmException,
			InvalidAlgorithmParameterException, InvalidKeyException,
			BadPaddingException, IllegalBlockSizeException {

		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		byte[] cipherText = cipher.doFinal(input.getBytes());
		return Base64.getEncoder().encodeToString(cipherText);
	}

	/** 
	 * String decryption.
	 * @param algorithm
	 * @param cipherText
	 * @param key
	 * @param iv
	 * @return
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidAlgorithmParameterException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public static String decrypt(String algorithm, String cipherText,
			SecretKey key, IvParameterSpec iv)
			throws NoSuchPaddingException, NoSuchAlgorithmException,
			InvalidAlgorithmParameterException, InvalidKeyException,
			BadPaddingException, IllegalBlockSizeException {

		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.DECRYPT_MODE, key, iv);
		byte[] plainText = cipher
				.doFinal(Base64.getDecoder().decode(cipherText));
		return new String(plainText);
	}
	
	// encryption and decryption of file is basically the same
	public static void encryptFile(String algorithm, SecretKey key, IvParameterSpec iv,
		    File inputFile, File outputFile) throws IOException, NoSuchPaddingException,
		    NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
		    BadPaddingException, IllegalBlockSizeException {
		    
		    Cipher cipher = Cipher.getInstance(algorithm);
		    cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		    FileInputStream inputStream = new FileInputStream(inputFile);
		    FileOutputStream outputStream = new FileOutputStream(outputFile);
		    byte[] buffer = new byte[64];
		    int bytesRead;
		    while ((bytesRead = inputStream.read(buffer)) != -1) {
		        byte[] output = cipher.update(buffer, 0, bytesRead);
		        if (output != null) {
		            outputStream.write(output);
		        }
		    }
		    byte[] outputBytes = cipher.doFinal();
		    if (outputBytes != null) {
		        outputStream.write(outputBytes);
		    }
		    inputStream.close();
		    outputStream.close();
		}
	
	public static void decryptFile(String algorithm, SecretKey key, IvParameterSpec iv,
		    File inputFile, File outputFile) throws IOException, NoSuchPaddingException,
		    NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
		    BadPaddingException, IllegalBlockSizeException {
		    
		    Cipher cipher = Cipher.getInstance(algorithm);
		    cipher.init(Cipher.DECRYPT_MODE, key, iv);
		    FileInputStream inputStream = new FileInputStream(inputFile);
		    FileOutputStream outputStream = new FileOutputStream(outputFile);
		    byte[] buffer = new byte[64];
		    int bytesRead;
		    while ((bytesRead = inputStream.read(buffer)) != -1) {
		        byte[] output = cipher.update(buffer, 0, bytesRead);
		        if (output != null) {
		            outputStream.write(output);
		        }
		    }
		    byte[] outputBytes = cipher.doFinal();
		    if (outputBytes != null) {
		        outputStream.write(outputBytes);
		    }
		    inputStream.close();
		    outputStream.close();
		}

	// https://docs.oracle.com/en/java/javase/11/docs/api/java.base/javax/crypto/KeyGenerator.html
	// n is 128, 192 or 256 for AES
	// generating key from key size
	public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

		keyGenerator.init(n);
		SecretKey key = keyGenerator.generateKey();
		return key;
	}

	// generating key from password + salt
	public static SecretKey getKeyFromPassword(String password, String salt)
			throws NoSuchAlgorithmException, InvalidKeySpecException {

		SecretKeyFactory factory = SecretKeyFactory
				.getInstance(secrectKeyAlgorithm);
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(),
				65536, 256);
		SecretKey secret = new SecretKeySpec(
				factory.generateSecret(spec).getEncoded(), "AES");
		return secret;
	}

	public static IvParameterSpec generateIv() {
		byte[] iv = new byte[16];
		new SecureRandom().nextBytes(iv);
		return new IvParameterSpec(iv);
	}
}
