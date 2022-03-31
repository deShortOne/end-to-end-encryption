package com.github.deShortOne.peer_to_peer_encryption;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;

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
    private void start(Stage stage) {
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
}
