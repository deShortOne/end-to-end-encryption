package com.github.deShortOne.peer_to_peer_encryption;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Login/ sign up page
 *
 */
public class StartScreen {

	public static Parent loginPage() {
		GridPane grid = new GridPane();
		grid.setHgap(10);
	    grid.setVgap(10);
	    grid.setPadding(new Insets(0, 10, 0, 10));
	    
	    Text signInMode = new Text("Log in");
	    signInMode.setId("SignInModeText");
	    signInMode.setFont(Font.font("Arial", FontWeight.BOLD, 20));
	    grid.add(signInMode, 0, 0);
	    
	    Text usernameText = new Text("Username");
	    grid.add(usernameText, 0, 1, 2, 1);
	    
	    TextField username = new TextField("Username");
	    grid.setId("usernameInput");
	    grid.add(username, 0, 2);
	    
	    Text passwordText = new Text("Password");
	    grid.add(passwordText, 0, 3, 2, 1);
	    
	    TextField passwordInput = new TextField("Password");
	    grid.setId("Password");
	    grid.add(passwordInput, 0, 4);
	    
	    Button signIn = new Button("Log in");
	    signIn.setId("LoginButton");
	    signIn.setOnAction(e -> {
	    	String s;
	    	if (signIn.getText().equals("Log in")) {
	    		s = "Sign in";
	    	} else {
	    		s = "Log in";
	    	}
	    	signInMode.setText(s);
    		signIn.setText(s);
	    });
	    grid.add(signIn, 0, 5);
	    
	    return grid;
	}
}
