package com.github.deShortOne.peer_to_peer_encryption;

import java.io.IOException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MessagePage extends Application {

	private Connection c;

	private TextField box2;
	
	private TextArea inputoutput;

	private Text outputMsg;

//	private Stage stage;

	public MessagePage() {
//		try {
//			c = new Connection(this);
//		} catch (IOException e) {
//			e.printStackTrace();
//			// Invalid Connection
//		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene s = new Scene(setupPage());
		primaryStage.setScene(s);
//		stage = primaryStage;
		primaryStage.show();

		try {
			c = new Connection(this);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		launch(args);
	}

	public Parent setupPage() {
		BorderPane root = new BorderPane();
		root.setCenter(messageWindow());
		return root;
	}

	public void recieveMessage(String msg) {
		inputoutput.appendText(msg);
	}

	public void setErrorMsg(String msg) {
		outputMsg.setText(msg);
	}

	public Parent messageWindow() {
		VBox root = new VBox();
//		root.prefWidthProperty().bind(stage.heightProperty().multiply(0.80));

		outputMsg = new Text();
		root.getChildren().add(outputMsg);
		
		// Text interactions
		inputoutput = new TextArea();
		inputoutput.setEditable(false);
		
		ScrollPane sp = new ScrollPane(inputoutput);

		root.getChildren().add(sp);
		
		
		TextField output = new TextField();
		output.setId("box1");
		
		Button b = new Button("Send msg");
		b.setId("SendMsgButton");
		b.setOnAction(e -> {
			try {
				c.sendMessage(output.getText());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		
		HBox sendRow = new HBox();
		sendRow.getChildren().addAll(output, b);
		
		root.getChildren().add(sendRow);

		return sp;
	}

	private Node translationPage() {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(0, 10, 0, 10));

		Text encryptText = new Text("Send/ recieve text");
		grid.add(encryptText, 0, 0);

		TextField box1 = new TextField();
		box1.setId("box1");
		grid.add(box1, 0, 1);

		box2 = new TextField();
		box2.setId("box2");
		grid.add(box2, 1, 1);

		Button b = new Button("Send msg");
		b.setId("SendMsgButton");
		b.setOnAction(e -> {
			try {
				c.sendMessage(box1.getText());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		grid.add(b, 0, 2);

		outputMsg = new Text("No connection to client");
		outputMsg.setId("ErrorMsg");

		grid.add(outputMsg, 0, 3);

		return grid;
	}
}
