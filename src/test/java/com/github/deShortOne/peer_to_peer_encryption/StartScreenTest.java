package com.github.deShortOne.peer_to_peer_encryption;

import java.io.FileWriter;
import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;

import com.opencsv.CSVWriter;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
public class StartScreenTest {

	private Parent nodeToScene;

	@Start
	public void start(Stage stage) {
		nodeToScene = StartScreen.loginPage();
		Scene s = new Scene(nodeToScene);
		stage.setScene(s);
		stage.show();
	}

	@Test
	public void buttonsAreCorrectAndSwitching(FxRobot robot) {
		Text signInText = null;
		Button signIn = null;

		for (Node p : nodeToScene.getChildrenUnmodifiable()) {
			if (p.getId() == null)
				continue;

			if (p.getId().equals("LoginButton")) {
				signIn = (Button) p;
			} else if (p.getId().equals("SignInModeText")) {
				signInText = (Text) p;
			}
		}
		Assertions.assertNotEquals(null, signInText);
		Assertions.assertNotEquals(null, signIn);

		Assertions.assertEquals("Log in", signInText.getText());
		Assertions.assertEquals("Log in", signIn.getText());

		robot.clickOn(signIn);
		Assertions.assertEquals("Sign in", signInText.getText());
		Assertions.assertEquals("Sign in", signIn.getText());

		robot.clickOn(signIn);
		Assertions.assertEquals("Log in", signInText.getText());
		Assertions.assertEquals("Log in", signIn.getText());
	}

	@Test
	public void checkUsernameAndPassword() throws IOException {
		// Clears csv file
		String csv = StartScreen.fileLoc;
		CSVWriter writer = new CSVWriter(new FileWriter(csv));
		writer.writeNext(null);
		writer.close();

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
}
