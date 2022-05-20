package com.github.deShortOne.peer_to_peer_encryption;

import javafx.scene.control.TextArea;

/**
 * Rewritten version of Connection.
 * 
 * @author deShortOne
 *
 */
public class ConversationPage {

	private TextArea inputOutput = new TextArea();;

	public ConversationPage() {
		setupTextArea();
	}

	public void addText(String text) {
		inputOutput.appendText(text + "\n");
	}
	
	public TextArea getMessages() {
		return inputOutput;
	}

	private void setupTextArea() {
		inputOutput.setEditable(false);
		inputOutput.setWrapText(true);
	}
}
