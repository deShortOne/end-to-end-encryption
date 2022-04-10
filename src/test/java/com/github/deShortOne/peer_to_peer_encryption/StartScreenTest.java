package com.github.deShortOne.peer_to_peer_encryption;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.opencsv.CSVWriter;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
public class StartScreenTest {

	private Parent nodeToScene;

	private Text signinLoginTitle = null;
	private Button login = null;
	private Button signup = null;
	private Text outputMsg = null;
	private TextField usernameInput = null;
	private TextField passwordInput = null;

	@Start
	public void start(Stage stage) {
		StartScreen s = new StartScreen();
		nodeToScene = s.loginPage();
		Scene s = new Scene(nodeToScene);
		stage.setScene(s);
		stage.show();

		try {
			resetAccountsCSV();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		setVariousNodes();
	}

	// Checks if file is created
	@Test
	public void checkIfFileCreated(FxRobot robot) throws IOException,
			NoSuchAlgorithmException, InvalidKeySpecException {

		String username = "hidden_User1";
		String password = "lowbattery";
		
		File pubFile = new File(RSAEncryption.publicKeyFileLoc + username + ".pubkey");
		File priFile = new File(RSAEncryption.publicKeyFileLoc+ username + ".prikey");
		pubFile.delete();
		priFile.delete();
		Assertions.assertFalse(pubFile.isFile());
		Assertions.assertFalse(priFile.isFile());
		
		resetAccountsCSV();

		robot.doubleClickOn(usernameInput).write(username);
		robot.doubleClickOn(passwordInput).write(password);

		robot.clickOn(signup);
		
		Assertions.assertTrue(pubFile.isFile());
		Assertions.assertTrue(priFile.isFile());
	}

	@Test
	public void buttonsAreCorrectAndLoggingInSigningIn(FxRobot robot)
			throws IOException, InterruptedException {

		// Switching text
		robot.clickOn(signup);
		Assertions.assertEquals("Sign up", signinLoginTitle.getText());

		robot.clickOn(login);
		Assertions.assertEquals("Log in", signinLoginTitle.getText());

		/*
		 * Below is basically same as checkUsernameAndPassword test but now
		 * being inputed to the window.
		 */
		resetAccountsCSV();

		String username = "hidden_User1";
		String password = "lowbattery";

		robot.doubleClickOn(usernameInput).write(username);
		robot.doubleClickOn(passwordInput).write(password);

		// No user with this username exist so login fails
		robot.clickOn(login);
		Assertions.assertEquals("Username or password incorrect",
				outputMsg.getText());

		// No user with this username exists so sign up succeeds
		robot.clickOn(signup);
		Assertions.assertEquals("Sign up success!", outputMsg.getText());

		// Username with correct password so log in succeeds
		robot.clickOn(login);
		Assertions.assertEquals("Success!", outputMsg.getText());

		// Already exists username with this username so sign up fails
		robot.clickOn(signup);
		Assertions.assertEquals("Username already taken", outputMsg.getText());

		// Username with incorrect password so login fails

		robot.doubleClickOn(passwordInput).write("asdf").clickOn(login);
		Assertions.assertEquals("Username or password incorrect",
				outputMsg.getText());
	}
	
	@Test
	public void emptyUsernameOrPassword(FxRobot robot) {
		robot.clickOn(signup);
		Assertions.assertEquals("Username cannot be empty", outputMsg.getText());
		
		robot.doubleClickOn(usernameInput).write("A").clickOn(signup);
		Assertions.assertEquals("Password cannot be empty", outputMsg.getText());
		robot.doubleClickOn(passwordInput).write(" ").clickOn(signup); //Space
		Assertions.assertEquals("Password cannot be empty", outputMsg.getText());
		
		
		robot.doubleClickOn(usernameInput).write(" 		 "); //Space tab tab space
		robot.doubleClickOn(passwordInput).write("");
		robot.clickOn(signup);
		Assertions.assertEquals("Username cannot be empty", outputMsg.getText());
	
	}
	
	@Test
	public void checkUsernameAndPassword() throws IOException,
			NoSuchAlgorithmException, InvalidKeySpecException {

		// resetAccountsCSV();

		String username = "hiddenUser1";
		String password = "lowbattery";

		// No user with this username exist so login fails
		Assertions.assertFalse(
				StartScreen.loginUsernameAndPassword(username, password));
		// No user with this username exists so sign up succeeds
		Assertions.assertTrue(
				StartScreen.signupUsernameAndPassword(username, password));
		// Username with correct password so log in succeeds
		Assertions.assertTrue(
				StartScreen.loginUsernameAndPassword(username, password));

		// Username with incorrect password so login fails
		Assertions.assertFalse(
				StartScreen.loginUsernameAndPassword(username, password + "a"));
		// Already exists username with this username so sign up fails
		Assertions.assertFalse(
				StartScreen.signupUsernameAndPassword(username, password));
	}

	private void setVariousNodes() {
		for (Node p : nodeToScene.getChildrenUnmodifiable()) {
			if (p.getId() == null)
				continue;

			switch (p.getId()) {
			case "LoginButton" -> login = (Button) p;
			case "SignupButton" -> signup = (Button) p;
			case "SignInModeText" -> signinLoginTitle = (Text) p;
			case "Output" -> outputMsg = (Text) p;
			case "UsernameInput" -> usernameInput = (TextField) p;
			case "PasswordInput" -> passwordInput = (TextField) p;
			}

		}

		Assertions.assertNotNull(signinLoginTitle);
		Assertions.assertNotNull(login);
		Assertions.assertNotNull(signup);
		Assertions.assertNotNull(outputMsg);
		Assertions.assertNotNull(usernameInput);
		Assertions.assertNotNull(passwordInput);

		Assertions.assertEquals("Log in", signinLoginTitle.getText());
		Assertions.assertEquals("Log in", login.getText());
		Assertions.assertEquals("Sign up", signup.getText());
		Assertions.assertEquals("", outputMsg.getText());
	}

	private void resetAccountsCSV() throws IOException {
		// Clears csv file
		String csv = StartScreen.fileLoc;
		CSVWriter writer = new CSVWriter(new FileWriter(csv));
		writer.writeNext(null);
		writer.close();
	}
}
