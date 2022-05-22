package com.github.deShortOne.peer_to_peer_encryption;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Exchange {

	/**
	 * Output to send to other node.
	 */
	private DataOutputStream output;
	
	/**
	 * Input to receive information from other node.
	 */
	private DataInputStream input;
	
	/**
	 * Checks if socket is set.
	 */
	private boolean setStream;
	
	/**
	 * Creates object without socket, cannot send or receive information.
	 */
	public Exchange() {
		setStream = false;
	}
	
	/**
	 * Creates object with socket.
	 * 
	 * @param socket
	 * @throws IOException
	 */
	public Exchange(Socket socket) throws IOException {
		setSocket(socket);
		setStream = true;
	}
	
	/**
	 * Sets socket of this object.
	 * 
	 * @param socket
	 * @throws IOException
	 */
	protected void setSocket(Socket socket) throws IOException {
		output = new DataOutputStream(socket.getOutputStream());
		input = new DataInputStream(socket.getInputStream());
		setStream = true;
	}
	
	/**
	 * 
	 * @param message
	 * @throws IOException called when sockets are not yet set
	 */
	protected void sendMessage(byte[] message) throws IOException {
		if (!setStream) {
			throw new IOException("Socket not configured");
		}
		
		output.writeInt(message.length);
		output.write(message);
	}
	
	protected void sendMessage(byte[][] message) throws IOException {
		for (byte[] b : message) {
			this.sendMessage(b);
		}
	}
	
	/**
	 * Receive message from socket.
	 * 
	 * @return
	 * @throws IOException
	 */
	protected byte[] recieveMessage() throws IOException {
		int lengthOfMessage = input.readInt();
		byte[] out = new byte[lengthOfMessage];
		input.readFully(out);
		return out;
	}
}