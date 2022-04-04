package com.github.deShortOne.peer_to_peer_encryption;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
public class MessagePageTest {

	private Parent nodesInScene1;	
	private TextField per1Box1;
	private TextField per1Box2;
	private Button per1SendBut;
	private Text per1OutputMsg;

//	private Parent nodesInScene2;
//	private TextField per2Box1;
//	private TextField per2Box2;
//	private Button per2SendBut;
//	private Text per2OutputMsg;
	
	@Start
	public void start(Stage stage) {
		MessagePage mp1 = new MessagePage();
//		nodesInScene1 = mp1.setupPage();
//		Scene s1 = new Scene(nodesInScene1);
//		stage.setScene(s1);
		stage.show();
//		
//		Stage per2 = new Stage();
//		MessagePage mp2 = new MessagePage();
//		nodesInScene2 = mp2.setupPage();
//		Scene s2 = new Scene(nodesInScene2);
//		per2.setScene(s2);
//		per2.show();
		
//		setVariousNodes();
	}
	
	@Test
	public void initialTest() {
		try {
			Thread.sleep(25);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setVariousNodes();
		Assertions.assertTrue(true);
	}
	
	private void setVariousNodes() {
		Assertions.assertNotNull(nodesInScene1);
		for (Node p : nodesInScene1.getChildrenUnmodifiable()) {
			if (p.getId() == null)
				continue;
			
			switch (p.getId()) {
			case "box1" -> per1Box1 = (TextField) p;
			case "box2" -> per1Box2 = (TextField) p;
			case "SendMsgButton" -> per1SendBut = (Button) p;
			case "ErrorMsg" -> per1OutputMsg = (Text) p;
			}
		}
		Assertions.assertNotNull(nodesInScene1);
		Assertions.assertNotNull(per1Box1);
		Assertions.assertNotNull(per1Box2);
		Assertions.assertNotNull(per1SendBut);
		Assertions.assertNotNull(per1OutputMsg);
		
//		Assertions.assertNotNull(nodesInScene2);
//		for (Node p : nodesInScene2.getChildrenUnmodifiable()) {
//			if (p.getId() == null)
//				continue;
//			
//			switch (p.getId()) {
//			case "box1" -> per1Box1 = (TextField) p;
//			case "box2" -> per1Box2 = (TextField) p;
//			case "SendMsgButton" -> per1SendBut = (Button) p;
//			case "ErrorMsg" -> per1OutputMsg = (Text) p;
//			}
//		}
//		Assertions.assertNotNull(nodesInScene2);
//		Assertions.assertNotNull(per2Box1);
//		Assertions.assertNotNull(per2Box2);
//		Assertions.assertNotNull(per2SendBut);
//		Assertions.assertNotNull(per2OutputMsg);
	}
}
