package com.github.deShortOne.peer_to_peer_encryption;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	private Stage stage;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		Scene startScreenScene = new Scene(StartScreen.loginPage());
		stage.setScene(startScreenScene);
		stage.show();
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}

