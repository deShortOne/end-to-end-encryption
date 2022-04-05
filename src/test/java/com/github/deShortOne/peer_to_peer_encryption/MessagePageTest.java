package com.github.deShortOne.peer_to_peer_encryption;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

@ExtendWith(ApplicationExtension.class)
public class MessagePageTest {

	private Parent mp1;
	private Text per1OutputMsg;
	private TextArea per1InputOutput;
	private TextField per1SendBox;
	private Button per1SendButton;
	
	private Parent mp2;
	private Text per2OutputMsg;
	private TextArea per2InputOutput;
	private TextField per2SendBox;
	private Button per2SendButton;

	@Start
	public void start(Stage stage) {
		MessagePage mp1 = new MessagePage();
		mp1.setStage(stage);
		stage.show();
		this.mp1 = stage.getScene().getRoot();

		Stage stage2 = new Stage();
		MessagePage mp2 = new MessagePage();
		mp2.setStage(stage2);
		stage2.show();
		this.mp2 = stage2.getScene().getRoot();
	}

	@Test
	public void initialTest() {
		setVariousNodes();
		Assertions.assertTrue(true);
	}

	private void setVariousNodes() {
		Assertions.assertNotNull(mp1);
		for (Node p : mp1.getChildrenUnmodifiable()) {
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
		
		Assertions.assertNotNull(mp2);
		for (Node p : mp2.getChildrenUnmodifiable()) {
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
