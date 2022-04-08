package com.github.deShortOne.peer_to_peer_encryption;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	private Stage stage;
	private StartScreen startScreen;

	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		startScreen = new StartScreen(this);
		Scene startScreenScene = new Scene(startScreen.loginPage());
		stage.setScene(startScreenScene);
		stage.show();
	}

	public void goMessagePage() {
		MessagePage mp = new MessagePage(startScreen.getCryptMessage(),
				startScreen.getName());
		mp.setStage(stage);
	}

	public void updateScene(Scene scene) {
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
