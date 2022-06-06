package com.github.deShortOne.peer_to_peer_encryption;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.github.deShortOne.end_to_end_encryption.Exchange;

public class ExchangeTest {

	@Test
	public void simpleExchangeOfInformation() throws IOException {
		ServerSocket aServer = new ServerSocket(8080);
		Socket bSocket = new Socket(InetAddress.getLoopbackAddress(), 8080);
		Socket aSocket = aServer.accept();

		Exchange ex1 = new Exchange(aSocket);
		Exchange ex2 = new Exchange();
		ex2.setSocket(bSocket);

		String msg = "unit tests suck to write";

		ex1.sendMessage(msg.getBytes());
		byte[] msgByte = ex2.recieveMessage();
		assertEquals(msg, new String(msgByte, StandardCharsets.UTF_8));

		aServer.close();
	}

	@Test
	public void errors() {
		Exchange ex1 = new Exchange();
		Exception error = assertThrows(IOException.class,
				() -> ex1.sendMessage(new byte[1]),
				"Expected sendMessage() to throw, but it didn't, socket should be null");
		assertTrue(error.getMessage().contains("Socket not configured"));
	}
}
