package com.ticket.console;

import org.beryx.textio.TextIO;
import org.beryx.textio.mock.MockTextTerminal;
import org.junit.Before;
import org.junit.Test;

public class TicketUserInterfaceTest {

	MockTextTerminal mockTextTerminal;
	TextIO textIO;
	TicketUserInterface ticketUI;
	
	@Before
	public void setUp(){
		mockTextTerminal = new MockTextTerminal();
		textIO = new TextIO(mockTextTerminal);
		ticketUI = new TicketUserInterface(textIO);
	}
	
	@Test
	public void testEmailInputReader() {
		//TODO: determine how to pass input into the mock text terminal by reading the demo 

	}

}
