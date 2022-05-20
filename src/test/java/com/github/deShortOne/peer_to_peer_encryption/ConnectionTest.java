package com.github.deShortOne.peer_to_peer_encryption;

import java.io.IOException;
import org.junit.jupiter.api.Test;

public class ConnectionTest {

	/**
	 * Isn't proper test as it ain't testing for anything but just to show how
	 * it works
	 * 
	 * @throws IOException
	 */
	@Test
	public void simpleServerClientConnection() throws IOException {

		Server s = new Server();

		Thread t1 = new Thread(() -> {
			try {
				Client a = new Client("A");
				a.sendMessage("B".getBytes());
				a.sendMessage("hii".getBytes());
				a.exit();
			} catch (IOException e) {
				e.printStackTrace();
			}

		});

		Thread t2 = new Thread(() -> {
			try {
				Client b = new Client("B");
				Thread.sleep(2000);
				// Hopefully should be long enough for message to be sent and
				// received
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
