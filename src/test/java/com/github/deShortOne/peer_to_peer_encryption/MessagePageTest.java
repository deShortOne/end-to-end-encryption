package com.github.deShortOne.peer_to_peer_encryption;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * IMPORTANT: run one test at a time! Unless there's a way, which I haven't
 * found yet to reset server and client!
 *
 */
@ExtendWith(ApplicationExtension.class)
public class MessagePageTest {

	private MessagePage mp1;
	private Parent mp1Parent;
	private Text per1OutputMsg;
	private TextArea per1InputOutput;
	private TextField per1SendBox;
	private Button per1SendButton;

	private MessagePage mp2;
	private Parent mp2Parent;
	private Text per2OutputMsg;
	private TextArea per2InputOutput;
	private TextField per2SendBox;
	private Button per2SendButton;

	@Start
	public void start(Stage stage) {
		mp1 = new MessagePage();
		mp1.setStage(stage);
		stage.show();
		this.mp1Parent = stage.getScene().getRoot();

		Stage stage2 = new Stage();
		mp2 = new MessagePage();
		mp2.setStage(stage2);
		stage2.show();
		stage2.setX(0);
		this.mp2Parent = stage2.getScene().getRoot();

		setVariousNodes();
	}

	// @Test
	public void aa_initialTest() {
		Assertions.assertTrue(true);
	}

	@Test
	public void testmessaging(FxRobot robot) {
		robot.clickOn(per1SendBox).write("Hii!").clickOn(per1SendButton);
		Assertions.assertEquals("", per1SendBox.getText());
		String serverMessages = "You: Hii!\n";
		Assertions.assertEquals(serverMessages, per1InputOutput.getText());

		String clientMessages = mp1.getName() + ": Hii!\n";
		Assertions.assertEquals(clientMessages, per2InputOutput.getText());

		robot.clickOn(per2SendBox).write("Sup!").clickOn(per2SendButton);
		Assertions.assertEquals("", per2SendBox.getText());
		clientMessages += "You: Sup!\n";
		Assertions.assertEquals(clientMessages, per2InputOutput.getText());
		
		serverMessages += mp2.getName() + ": Sup!\n";
		Assertions.assertEquals(serverMessages, per1InputOutput.getText());
	}

	private void setVariousNodes() {
		Assertions.assertNotNull(mp1Parent);
		for (Node p : mp1Parent.getChildrenUnmodifiable()) {
			if (p.getId() == null)
				continue;
			if (p.getId().equals("messageWindow")) {
				for (Node messageWindow : ((Parent) p)
						.getChildrenUnmodifiable()) {
					if (messageWindow.getId() == null)
						continue;
					if (messageWindow.getId().equals("scrollpane")) {
						per1InputOutput = (TextArea) ((ScrollPane) messageWindow)
								.getContent();
					} else if (messageWindow.getId().equals("sendrow")) {

						for (Node p3 : ((Parent) messageWindow)
								.getChildrenUnmodifiable()) {

							if (p3.getId() == null)
								continue;
							switch (p3.getId()) {
							case "sendArea" -> per1SendBox = (TextField) p3;
							case "SendMsgButton" -> per1SendButton = (Button) p3;
							}
						}
					} else {
						switch (messageWindow.getId()) {
						case "ErrorMsg" -> per1OutputMsg = (Text) messageWindow;
						}
					}
				}
			}
		}
		Assertions.assertNotNull(per1OutputMsg);
		Assertions.assertNotNull(per1InputOutput);
		Assertions.assertNotNull(per1SendBox);
		Assertions.assertNotNull(per1SendButton);

		/**/

		Assertions.assertNotNull(mp2Parent);
		for (Node p : mp2Parent.getChildrenUnmodifiable()) {
			if (p.getId() == null)
				continue;
			if (p.getId().equals("messageWindow")) {
				for (Node messageWindow : ((Parent) p)
						.getChildrenUnmodifiable()) {
					if (messageWindow.getId() == null)
						continue;
					if (messageWindow.getId().equals("scrollpane")) {
						per2InputOutput = (TextArea) ((ScrollPane) messageWindow)
								.getContent();
					} else if (messageWindow.getId().equals("sendrow")) {

						for (Node p3 : ((Parent) messageWindow)
								.getChildrenUnmodifiable()) {

							if (p3.getId() == null)
								continue;
							switch (p3.getId()) {
							case "sendArea" -> per2SendBox = (TextField) p3;
							case "SendMsgButton" -> per2SendButton = (Button) p3;
							}
						}
					} else {
						switch (messageWindow.getId()) {
						case "ErrorMsg" -> per2OutputMsg = (Text) messageWindow;
						}
					}
				}
			}
		}
		Assertions.assertNotNull(per2OutputMsg);
		Assertions.assertNotNull(per2InputOutput);
		Assertions.assertNotNull(per2SendBox);
		Assertions.assertNotNull(per2SendButton);
	}
}
