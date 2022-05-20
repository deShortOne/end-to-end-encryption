package com.github.deShortOne.peer_to_peer_encryption;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Exchange {

	private DataOutputStream output;
	private DataInputStream input;
	
	private boolean setStream;
	
	public Exchange() {
		setStream = false;
	}
	
	protected void setSocket(Socket socket) throws IOException {
		output = new DataOutputStream(socket.getOutputStream());
		input = new DataInputStream(socket.getInputStream());
		setStream = true;
	}
	
	/**
	 * 
	 * @param message
	 * @throws IOException called when sockets are not yet set
	 * TODO make byte[] arg
	 */
	protected void sendMessage(byte[] message) throws IOException {
		if (!setStream) {
			throw new IOException("Socket not configured");
		}
		
		output.writeInt(message.length);
		output.write(message);
	}
	
	protected byte[] recieveMessage() throws IOException {
		int lengthOfMessage = input.readInt();
		byte[] out = new byte[lengthOfMessage];
		input.readFully(out);
		return out;
	}
}