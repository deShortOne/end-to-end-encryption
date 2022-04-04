package com.github.deShortOne.peer_to_peer_encryption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.junit.jupiter.api.Test;

public class SocketJTest {

	@Test
    public void testSimplePayload() throws IOException {
        byte[] emptyPayload = new byte[1001];

        // Using Mockito
        final Socket socket = mock(Socket.class);
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(byteArrayOutputStream);

        SocketJ text = new SocketJ(emptyPayload) {
            @Override
            public Socket createSocket() {
                return socket;
            }
        };

        assertEquals("Message sent successfully", text.sendTo("localhost", 1234));
        assertEquals("whatever you wanted to send".getBytes(), byteArrayOutputStream.toByteArray());
    }
	
}
