package com.github.deShortOne.peer_to_peer_encryption;

import java.security.PublicKey;

public class ClientAccount extends Account {

	private ConversationPage cp;
	
	public ClientAccount(PublicKey publicKey, ConversationPage conversationPage, Exchange exchange) {
		super(publicKey, exchange);
		cp = conversationPage;
	}
	
	public ConversationPage getConversationPage() {
		return cp;
	}
	
	public void addMessage(String message) {
		cp.addText(message);
	}
}
