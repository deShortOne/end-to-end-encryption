package com.github.deShortOne.peer_to_peer_encryption;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class CryptMessage {

	private RSAEncryption rsa;

	public CryptMessage(RSAEncryption rsa) {
		this.rsa = rsa;

	}

	public void joinFriend(String name) {

	}

	public String saveMessage(String clearMessage)
			throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, InvalidAlgorithmParameterException {

		return null;
	}

	/**
	 * Returned byte[] should be sent to reciever
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
	public static byte[][] sendMessage(String clearMessage, PublicKey pubKey)
			throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, InvalidAlgorithmParameterException {

		String algorithm = "AES/CBC/PKCS5Padding";

		SecretKey key = AESEncryption.generateKey(256);
		IvParameterSpec iv = AESEncryption.generateIv();

		StringBuilder sb = new StringBuilder();
		sb.append(Base64.getEncoder().encodeToString(key.getEncoded()));
		sb.append(Base64.getEncoder().encodeToString(iv.getIV()));

		byte[] base = RSAEncryption.encrypt(sb.toString(), pubKey);

		byte[] encryptedMsg = AESEncryption.encrypt(algorithm, clearMessage,
				key, iv);

		return new byte[][] { base, encryptedMsg };
	}

	public String recieveMessage(byte[] base, byte[] cipherMessage)
			throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException {

		return null;
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
	public static String recieveMessage(byte[] cipherBase, byte[] cipherMessage,
			PrivateKey priKey)
			throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, InvalidAlgorithmParameterException {

		String algorithm = "AES/CBC/PKCS5Padding";

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

		String clearMsg = AESEncryption.decrypt(algorithm, cipherMessage, key,
				iv);
		return clearMsg;
	}
}
