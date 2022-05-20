package com.github.deShortOne.peer_to_peer_encryption;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
public class ConnectionTest {
	
	Server s;
	MessageWindow mw;
	
	@Start
	public void start(Stage s0) throws Exception {
		s = new Server();
		mw = new MessageWindow();
		mw.start(s0);
		
	}

	/**
	 * Isn't proper test as it ain't testing for anything but just to show how
	 * it works
	 * 
	 * @throws IOException
	 */
	@Test
	public void simpleServerClientConnection() throws IOException {

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
}
