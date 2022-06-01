package com.github.deShortOne.end_to_end_encryption;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.NoSuchPaddingException;

import com.baeldung.encryption.CryptMessage;
import com.baeldung.encryption.RSAEncryption;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Login/ sign up page
 *
 */
public class StartScreen {

	public static String fileLoc = "files\\accounts.csv";

	public static Parent loginPage() {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(0, 10, 0, 10));

		Text signInMode = new Text("Log in");
		signInMode.setId("SignInModeText");
		signInMode.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		grid.add(signInMode, 0, 0);

		Text usernameText = new Text("Username");
		grid.add(usernameText, 0, 1, 2, 1);

		TextField usernameInput = new TextField();
		usernameInput.setId("UsernameInput");
		grid.add(usernameInput, 0, 2);

		Text passwordText = new Text("Password");
		grid.add(passwordText, 0, 3, 2, 1);

		// Use TextField for clear password, PasswordField to hide password
		TextField passwordInput = new PasswordField();
		passwordInput.setId("PasswordInput");
		grid.add(passwordInput, 0, 4);

		Text outputMsg = new Text("");
		outputMsg.setId("Output");
		grid.add(outputMsg, 0, 5, 2, 1);

		Button login = new Button("Log in");
		login.setId("LoginButton");
		login.setOnAction(e -> {
			if (!signInMode.getText().equals("Log in")) {
				signInMode.setText("Log in");
			}
			boolean goodInput = loginUsernameAndPassword(
					usernameInput.getText(), passwordInput.getText());
			if (goodInput) {
				try {
					RSAEncryption rsaEncryption = new RSAEncryption(
							usernameInput.getText(), passwordInput.getText(),
							false);
					outputMsg.setText("Success!");
					moveToNextWindow(rsaEncryption);
				} catch (NoSuchAlgorithmException | InvalidKeySpecException
						| IOException | NoSuchPaddingException e1) {
					e1.printStackTrace();
					outputMsg.setText("RSA Encryption error!");
				}
			} else {
				outputMsg.setText("Username or password incorrect");
			}

		});
		grid.add(login, 0, 6);

		Button signIn = new Button("Sign up");
		signIn.setId("SignupButton");
		signIn.setOnAction(e -> {
			if (!signInMode.getText().equals("Sign up")) {
				signInMode.setText("Sign up");
				outputMsg.setText("");
			}

			try {
				boolean goodInput = signupUsernameAndPassword(
						usernameInput.getText(), passwordInput.getText());

				if (goodInput) {
					RSAEncryption rsaEncryption = new RSAEncryption(
							usernameInput.getText(), passwordInput.getText(),
							true);
					moveToNextWindow(rsaEncryption);
					outputMsg.setText("Sign up success!");
				} else {
					if (blankInput(usernameInput.getText()))
						outputMsg.setText("Username cannot be empty");
					else if (blankInput(passwordInput.getText()))
						outputMsg.setText("Password cannot be empty");
					else
						outputMsg.setText("Username already taken");
				}
			} catch (NoSuchAlgorithmException | InvalidKeySpecException
					| IOException | NoSuchPaddingException e1) {
				e1.printStackTrace();
				outputMsg.setText("Critical error");
			}
		});
		grid.add(signIn, 1, 6);

		return grid;
	}

	public static void moveToNextWindow(RSAEncryption rsaEncryption) {
		CryptMessage cm = new CryptMessage(rsaEncryption);
		
		
		
		cm.getClass();
	}

	public static boolean loginUsernameAndPassword(String username,
			String password) {

		String hashPassword = calculatePasswordHash(username, password);
		if (hashPassword == null)
			return false;

		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(fileLoc));
		} catch (FileNotFoundException e) {
			// no password file = no saved accounts
			e.printStackTrace();
			return false;
		}

		String hashUsername = calculateUsernameHash(username);
		String[] nextLine;
		try {
			while ((nextLine = reader.readNext()) != null) {
				if (nextLine[0].equals(hashUsername)) {
					return nextLine[1].equals(hashPassword);
				}
			}
		} catch (CsvValidationException | IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean signupUsernameAndPassword(String username,
			String password) throws NoSuchAlgorithmException,
			FileNotFoundException, InvalidKeySpecException, IOException {

		if (blankInput(username) || blankInput(password))
			return false;

		String hashUsername = calculateUsernameHash(username);
		if (checkForDuplicate(hashUsername))
			return false;

		String hashPassword = calculatePasswordHash(username, password);
		if (hashPassword == null)
			return false;

		FileWriter mFileWriter;
		try {
			mFileWriter = new FileWriter(fileLoc, true);
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}

		CSVWriter writer = new CSVWriter(mFileWriter);
		writer.writeNext(new String[] { hashUsername, hashPassword });
		try {
			writer.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private static boolean checkForDuplicate(String username) {
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(fileLoc));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}

		String[] nextLine;
		try {
			while ((nextLine = reader.readNext()) != null) {
				if (nextLine[0].equals(username)) {
					return true;
				}
			}
		} catch (CsvValidationException | IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String calculateUsernameHash(String username) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		byte[] encodedhash = digest
				.digest(username.getBytes(StandardCharsets.UTF_8));
		return cleanHash(new String(encodedhash, StandardCharsets.UTF_8));
	}

	private static String calculatePasswordHash(String username,
			String password) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		byte[] encodedhash = digest
				.digest(password.getBytes(StandardCharsets.UTF_8));

		String tmp1 = new String(encodedhash, StandardCharsets.UTF_8)
				+ username;

		byte[] encodedhash1 = digest
				.digest(tmp1.getBytes(StandardCharsets.UTF_8));
		return cleanHash(new String(encodedhash1, StandardCharsets.UTF_8));
	}

	private static String cleanHash(String hash) {
		StringBuilder sb = new StringBuilder();
		for (char c : hash.toCharArray()) {
			if (c == '\\') {

			} else {
				sb.append(c);
			}
		}

		return sb.toString();
	}

	private static boolean blankInput(String input) {
		if (input.equals(""))
			return true;

		Pattern pattern = Pattern.compile("\\s+");
		Matcher matcher = pattern.matcher(input);

		return matcher.matches();
	}
}
