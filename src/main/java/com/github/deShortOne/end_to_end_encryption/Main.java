package com.github.deShortOne.end_to_end_encryption;

import com.baeldung.encryption.CryptMessage;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	private Stage stage;
	
	public static String contacts = "./contacts/";
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		Scene startScreenScene = new Scene(StartScreen.loginPage());
		stage.setScene(startScreenScene);
		stage.show();
	}
	
	public void mainScreen(CryptMessage cryptMessage) {
		MessageWindow mw = new MessageWindow();
	}
	
	public void updateScene(Scene scene) {
		stage.setScene(scene);
		stage.show();
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}

