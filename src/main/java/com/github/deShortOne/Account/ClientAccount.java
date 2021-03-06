package com.github.deShortOne.Account;

import java.io.IOException;
import java.security.PublicKey;

import com.github.deShortOne.ClientConnection.ConversationPage;

public class ClientAccount extends Account {

	private String nameOfRecipitent;

	private ConversationPage conversationPage;

	/**
	 * Connection to other person via this server connection.
	 */
	private ServerAccount serverConnection;

	/**
	 * New client connection to other client using public key to encrypt
	 * information to other client, stores conversation and stores connection to
	 * server in order to talk to other clients.
	 * 
	 * @param publicKey		public key for other client
	 * @param serverAccount	connection to server
	 */
	public ClientAccount(String nameOfRecipitent, PublicKey publicKey, ServerAccount serverAccount) {
		this(nameOfRecipitent, publicKey, new ConversationPage(), serverAccount);
	}

	/**
	 * Client connection to other client using public key to encrypt information
	 * to other client, stores conversation, and stores connection to server in
	 * order to talk to other clients.
	 * 
	 * @param publicKey        public key for other client
	 * @param conversationPage conversation page between client and other client
	 * @param serverAccount    connection to server
	 */
	public ClientAccount(String nameOfRecipitent, PublicKey publicKey, ConversationPage conversationPage,
			ServerAccount serverAccount) {
		super(publicKey);
		this.conversationPage = conversationPage;
		serverConnection = serverAccount;
		this.nameOfRecipitent = nameOfRecipitent;
	}

	/**
	 * Returns conversation page.
	 * 
	 * @return
	 */
	public ConversationPage getConversationPage() {
		return conversationPage;
	}

	/**
	 * Add message to current conversation page.
	 * 
	 * @param cMessage
	 */
	public void addMessage(String cMessage) {
		conversationPage.addText(cMessage);
	}

	/**
	 * Sends message to recipient.
	 * 
	 * @param cMsg
	 * @throws IOException
	 */
	public void sendMessage(String cMsg) throws IOException {
		addMessage(cMsg);
		byte[] encrpytedMsg = super.encryptMessage(cMsg.getBytes());
		serverConnection.sendMessage(nameOfRecipitent, encrpytedMsg);
	}
}
