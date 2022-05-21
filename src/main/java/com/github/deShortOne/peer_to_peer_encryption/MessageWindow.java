package com.github.deShortOne.peer_to_peer_encryption;

import java.io.IOException;
import java.net.ConnectException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Rewritten version of MessagePage.
 * 
 * @author deShortOne
 *
 */
public class MessageWindow extends Application {

	private Client client;

	private ScrollPane inputoutputScroll;

	private Text outputMsg;

	private TextField output;

	private String currentTalkingToPerson;

	private VBox contactsListRoot;

	// Alternate name between A and B.
	static String name = "A";

	/**
	 * For running when called from main.
	 */
	public MessageWindow() {
		this(name);
	}
	
	public MessageWindow(String username) {
		try {
			client = new Client(username, this);
		} catch (ConnectException e) {
			System.err.println("Server not live!");
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("ASDf");
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene s = new Scene(setupPage());
		primaryStage.setScene(s);
		primaryStage.show();
		primaryStage.setTitle(name);
	}

	public static void main(String[] args) {
		launch(new String[] { "a" });
	}

	private Parent setupPage() {
		BorderPane root = new BorderPane();
		root.setId("root");
		root.setCenter(messageWindow());
		root.setLeft(contactsList());
		return root;
	}

	private Parent messageWindow() {
		VBox root = new VBox();
		root.setId("messageWindow");

		outputMsg = new Text();
		outputMsg.setId("ErrorMsg");
		root.getChildren().add(outputMsg);

		inputoutputScroll = new ScrollPane();
		inputoutputScroll.setId("scrollpane");
		inputoutputScroll.setHbarPolicy(ScrollBarPolicy.NEVER);
		inputoutputScroll.setVbarPolicy(ScrollBarPolicy.NEVER);

		root.getChildren().add(inputoutputScroll);

		output = new TextField();
		output.setId("sendArea");

		Button b = new Button("Send msg");
		b.setId("SendMsgButton");
		b.setOnAction(e -> {
			try {
				sendMessage();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		HBox sendRow = new HBox();
		sendRow.setId("sendrow");
		sendRow.getChildren().addAll(output, b);

		root.getChildren().add(sendRow);

		return root;
	}

	private void sendMessage() throws IOException {
		String msg = output.getText();
		if (msg.equals(""))
			return;

		output.setText("");
		Pattern pattern = Pattern.compile("\\s+");
		Matcher matcher = pattern.matcher(msg);

		if (!matcher.matches()) {
			client.sendMessage(currentTalkingToPerson, msg);

		}
	}

	private Parent contactsList() {
		contactsListRoot = new VBox();
		Label l = new Label("Contacts    ");
		contactsListRoot.getChildren().add(l);
		return contactsListRoot;
	}

	public void addContact(String name) {
		Platform.runLater(() -> {
			Button b = new Button(name);
			b.setOnAction(e -> {
				setCurrContact(name);
			});
			contactsListRoot.getChildren().add(b);
		});
		
	}

	private void setCurrContact(String name) {
		inputoutputScroll.setContent(client.getMessages(name).getMessages());
		currentTalkingToPerson = name;

	}
}
