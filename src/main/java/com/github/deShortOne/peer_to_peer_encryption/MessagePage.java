package com.github.deShortOne.peer_to_peer_encryption;

import java.io.IOException;
import java.net.SocketException;
import java.util.regex.*;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

import java.util.Random;

public class MessagePage extends Application {

	private Connection c;

	private TextField output;

	private TextArea inputoutput;

	private Text outputMsg;
	
	private CryptMessage cm;
	
	private static Random rand = new Random();
	
	/**
	 * Testing. username of self.
	 */
	private String name = String.valueOf(rand.nextInt(10));

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
	 * @param cm
	 */
	public MessagePage(CryptMessage cm) {
		this.cm = cm;
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
			c = new Connection(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	private Parent setupPage() {
		BorderPane root = new BorderPane();
		root.setCenter(messageWindow());
		return root;
	}

	public void recieveMessage(String msg) {
//		if (msg.split(": ").length != 1)
			inputoutput.appendText(msg + "\n");
	}

	public void setErrorMsg(String msg) {
		outputMsg.setText(msg);
	}
	
	public String getName() {
		return name;
	}

	private Parent messageWindow() {
		VBox root = new VBox();

		outputMsg = new Text();
		root.getChildren().add(outputMsg);

		// Text interactions
		inputoutput = new TextArea();
		inputoutput.setEditable(false);
		inputoutput.setWrapText(true);

		ScrollPane sp = new ScrollPane(inputoutput);
		sp.setHbarPolicy(ScrollBarPolicy.NEVER);
		sp.setVbarPolicy(ScrollBarPolicy.NEVER);

		root.getChildren().add(sp);

		output = new TextField();
		output.setId("box1");

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
			c.sendMessage(msg);
			recieveMessage("You: " + msg);
		} 
	}
}
