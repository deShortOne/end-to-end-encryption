package com.github.deShortOne.peer_to_peer_encryption;

import java.io.IOException;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.crypto.NoSuchPaddingException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Random;

public class MessagePage extends Application {

	private KnockKnock newConnections;
	private Connection currConnection;

	private TextField output;

	private TextArea inputoutput;

	private Text outputMsg;

	private CryptMessage cm;

	private static Random rand = new Random();

	private static String[] fakeNames = new String[] { "Alice", "Bob",
			"Charlie", "Dieago", "Faizan", "Ghozi", "Holly", "Imogen", "Julia",
			"Kieren" };
	/**
	 * Testing. username of self.
	 */
	private String name = fakeNames[rand.nextInt(10)];

	/**
	 * HashMap<name, connection to that person>
	 */
	private HashMap<String, Connection> connections = new HashMap<>();

	private VBox contactsListRoot;

	/**
	 * Testing
	 */
	public MessagePage() {
//		try {
//			c = new Connection(this);
//		} catch (IOException e) {
//			e.printStackTrace();
//			// Invalid Connection
//		}
	}

	/**
	 * Real one.
	 * 
	 * @param cm
	 */
	public MessagePage(CryptMessage cm) {
		this.cm = cm;
	}

	public void setStage(Stage stage) {
		// should be passed in
		try {
			cm = new CryptMessage(new RSAEncryption(name, name, true));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException
				| NoSuchPaddingException | IOException e1) {
			e1.printStackTrace();
		}

		Scene s = new Scene(setupPage());
		
		s.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
			if (key.getCode() == KeyCode.ENTER) {
				try {
					sendMessage();
				} catch (SocketException e) {
					outputMsg.setText("Connection lost");
				} catch (IOException f) {
					f.printStackTrace();
				}
			}
		});

		stage.setScene(s);
		stage.show();
		stage.setTitle(name);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// should be passed in
		cm = new CryptMessage(new RSAEncryption(name, name, true));
		/**/

		Scene s = new Scene(setupPage());

		s.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
			if (key.getCode() == KeyCode.ENTER) {
				try {
					sendMessage();
				} catch (SocketException e) {
					outputMsg.setText("Connection lost");
				} catch (IOException f) {
					f.printStackTrace();
				}
			}
		});

		primaryStage.setScene(s);
		primaryStage.show();
		primaryStage.setTitle(name);
		try {
			newConnections = new KnockKnock(this, cm);
			System.out.println("ASDF");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * 
	 * @param name
	 */
	public void addConnection(String name, Connection newConnection) {
		connections.put(name, newConnection);
		Platform.runLater(() -> {
			addContact(name);
		});
		setCurrContact(name);
	}

	public void recieveMessage(String msg) {
		inputoutput.appendText(msg + "\n");
	}

	public void setErrorMsg(String msg) {
		outputMsg.setText(msg);
	}

	public String getName() {
		return name;
	}

	public byte[] getPublicKey() {
		return cm.getPublicKey();
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

		// Text interactions
		inputoutput = new TextArea();
		inputoutput.setId("inputoutput");
		inputoutput.setEditable(false);
		inputoutput.setWrapText(true);

		ScrollPane sp = new ScrollPane();
		sp.setId("scrollpane");
		sp.setHbarPolicy(ScrollBarPolicy.NEVER);
		sp.setVbarPolicy(ScrollBarPolicy.NEVER);
		sp.setContent(inputoutput);

		root.getChildren().add(sp);

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
		if (msg == "")
			return;

		output.setText("");
		Pattern pattern = Pattern.compile("\\s+");
		Matcher matcher = pattern.matcher(msg);

		if (!matcher.matches()) {
			currConnection.sendMessageEncrypted(msg);
			recieveMessage("You: " + msg);
		}
	}

	private Parent contactsList() {
		contactsListRoot = new VBox();
		Label l = new Label("Contacts    ");
		contactsListRoot.getChildren().add(l);
		return contactsListRoot;
	}

	private void addContact(String name) {
		Button b = new Button(name);
		b.setOnAction(e -> {
			setCurrContact(name);
		});
		contactsListRoot.getChildren().add(b);
	}

	private void setCurrContact(String name) {
		currConnection = connections.get(name);
	}
}
