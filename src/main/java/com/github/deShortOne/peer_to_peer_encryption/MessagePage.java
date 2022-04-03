package com.github.deShortOne.peer_to_peer_encryption;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MessagePage extends Application {
	
	private Connection c;
	
	private TextField box2;
	
//	public MessagePage() {
//		BorderPane root = new BorderPane();
//		root.setCenter(translationPage());
//		Scene s = new Scene(root);
//		Stage stage = new Stage();
//		stage.setScene(s);
//		stage.show();
//		
//		try {
//			c = new Connection(this);
//		} catch (IOException e) {
//			e.printStackTrace();
//			// Invalid Connection
//		}
//	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		System.out.println(1);
		BorderPane root = new BorderPane();
		System.out.println(2);
		root.setCenter(translationPage());
		System.out.println(3);
		Scene s = new Scene(root);
		System.out.println(4);
		
		primaryStage.setScene(s);
		primaryStage.show();
		
		try {
			c = new Connection(this);
		} catch (IOException e) {
			e.printStackTrace();
			// Invalid Connection
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	
	public void recieveMessage(String msg) {
		box2.setText(msg);
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
		b.setOnAction(e -> {
			try {
				c.sendMessage(box1.getText());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		grid.add(b, 2, 0);
		
		return grid;
	}
}
