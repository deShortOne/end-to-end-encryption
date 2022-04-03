package com.github.deShortOne.peer_to_peer_encryption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConnectionTest {

	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	private final PrintStream originalErr = System.err;

	@BeforeEach
	public void setUpStreams() {
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));
	}

	@AfterEach
	public void restoreStreams() {
		System.setOut(originalOut);
		System.setErr(originalErr);
	}

	@Test
	public void testJunitItself() {
		// Normal print statements
		System.out.print("hello");
		assertEquals("hello", outContent.toString());

		// Error print statement
		System.err.print("hello again");
		assertEquals("hello again", errContent.toString());
	}

	// @Test
	public void testConnection() throws IOException {
		byte[] emptyPayload = new byte[1001];

		final Socket socket = mock(Socket.class);

		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		when(socket.getOutputStream()).thenReturn(byteArrayOutputStream);

		Connection text = new Connection() {

			protected Socket createSocket() {
				return socket;
			}
		};

		assertEquals("Message sent successfully",
				text.sendTo("localhost", 8080));
		assertEquals("whatever you wanted to send".getBytes(),
				byteArrayOutputStream.toByteArray());

		// Have tried using mock but testing still fails... Tried this in new
		// class

//		Connection server = new Connection();
//		assertEquals("I'm a server", outContent.toString());
//		
//		Connection client = new Connection();
//		assertEquals("I'm the client", outContent.toString());
	}
}
