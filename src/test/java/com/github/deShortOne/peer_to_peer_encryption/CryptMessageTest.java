package com.github.deShortOne.peer_to_peer_encryption;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CryptMessageTest {

	HashMap<String, PublicKey> friendsList = new HashMap<>();

	//@Test
	public void sendMessage_onePerson()
			throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, FileNotFoundException, InvalidKeySpecException,
			IOException, InvalidAlgorithmParameterException {
		RSAEncryption friend1 = new RSAEncryption("", "", true);
		// friendsList.get("friend1.pub");

		String msg = "HI";
		byte[][] sentMessage = CryptMessage.sendMessage(msg,
				friend1.getPublicKey());
		String recievedMessage = CryptMessage.recieveMessage(sentMessage[0],
				sentMessage[1], friend1.getPrivateKey());

		Assertions.assertEquals(msg, recievedMessage);
	}
	
	@Test
	public void makeFriends()
			throws InvalidKeyException, NoSuchAlgorithmException, FileNotFoundException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		// Create new RSA
		RSAEncryption per1 = new RSAEncryption("per1", "AA", true); 
		RSAEncryption per2 = new RSAEncryption("per2", "BB", true);
		
		// Load profiles
		CryptMessage p1 = new CryptMessage(new RSAEncryption("per1", "AA", false));
		CryptMessage p2 = new CryptMessage(new RSAEncryption("per2", "AA", false));
		
		// p1 wants to make friends with p2
		
	}

	public void start() throws IOException, NoSuchAlgorithmException,
			InvalidKeySpecException {
		String fileLoc = "files\\contacts\\";

		File file = new File(fileLoc);
		Assertions.assertTrue(file.isDirectory());

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		for (File f : file.listFiles()) {
			Assertions.assertTrue(f.isFile());

			byte[] publicKeyBytes = Files.readAllBytes(f.toPath());

			EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
					publicKeyBytes);
			PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

			friendsList.put(f.getName(), publicKey);
		}

		Assertions.assertFalse(friendsList.isEmpty());
	}
}
