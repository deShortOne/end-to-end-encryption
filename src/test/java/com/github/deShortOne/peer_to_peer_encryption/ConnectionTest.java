package com.github.deShortOne.peer_to_peer_encryption;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
public class ConnectionTest {

	Server s;
	MessageWindow mw;

	@Start
	public void start(Stage s0) throws Exception {
		s = new Server();
	}

	/**
	 * Isn't proper test as it ain't testing for anything but just to show how
	 * it works
	 * 
	 * @throws Exception
	 */

	public void simpleServerClientConnection() throws Exception {
		mw = new MessageWindow();
		mw.start(new Stage());
		Thread t1 = new Thread(() -> {
			try {
				Client a = new Client("A", mw);
				a.sendMessage("B".getBytes());
				a.sendMessage("hii".getBytes());

				Thread.sleep(2000);
				// Hopefully should be long enough for message to be sent and
				// received

				a.exit();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		});

		Thread t2 = new Thread(() -> {
			try {
				Client b = new Client("B", mw);
				Thread.sleep(2000);
				// Hopefully should be long enough for message to be sent and
				// received

				b.sendMessage("A".getBytes());
				b.sendMessage("hiyaa".getBytes());
				b.exit();
			} catch (IOException e) {
				System.out.println("B");
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		t1.start();
		t2.start();

		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void simpleGUIAttempt(FxRobot robot) {
		new Thread(() -> {
			try {
				MessageWindow mw1 = new MessageWindow("A");
				
				Platform.runLater(() -> {
					Stage s1 = new Stage();
					try {
						mw1.start(s1);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					try {
						Thread.sleep(2000);
						// allow other thread to initialise and connect to server
						// will need to add in extra button to click on other person
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					TextField send = null;
					Button sendButton = null;
					TextArea messageArea = null;

					ObservableList<Node> nodeList = s1.getScene().getRoot()
							.getChildrenUnmodifiable();
					for (Node p1 : nodeList) {
						if (p1 == null | p1.getId() == null)
							continue;
						if (p1.getId().equals("messageWindow")) {
							for (Node p2 : ((Parent) p1)
									.getChildrenUnmodifiable()) {
								if (p2 == null || p2.getId() == null)
									continue;
								System.out.println(p2.getId());
								switch (p2.getId()) {
								
								case "sendrow" -> {
									for (Node p3 : ((Parent) p2).getChildrenUnmodifiable()) {
										switch (p3.getId()) {
										case "sendArea" -> send = (TextField) p3;
										case "SendMsgButton" -> sendButton = (Button) p3;
										}
									}
								}
								case "scrollpane" -> {
									messageArea = (TextArea) ((ScrollPane) p2)
										.getContent();
									System.out.println("Scrollpane found");
								}
								}
							}
						}
					}
					assertNotNull(send);
					assertNotNull(sendButton);

					// will be null bc no conversation page has yet been created for 
					// this GUI since there is no one to talk to.
					assertNotNull(messageArea);

					robot.clickOn(send).write("Hii").clickOn(sendButton);

					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// Hopefully should be long enough for message to be sent and
					// received
				});
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
}
}
