package com.ticket.console;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.function.BiConsumer;

import org.beryx.textio.EnumInputReader;
import org.beryx.textio.IntInputReader;
import org.beryx.textio.StringInputReader;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.mock.MockTextTerminal;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class TicketUserInterfaceTest {

	MockTextTerminal terminal;
	TextIO textIO;
	TicketUserInterface ticketUI;
	List<String> inputs;
	
	@Before
	public void setUp(){
		terminal = new MockTextTerminal();
		inputs = terminal.getInputs();
		textIO = new TextIO(terminal);
		ticketUI = new TicketUserInterface(textIO);
	}
	
	@Test
	public void testEmailInputReader(){
		String invalidEmail = "invalid";
		String customerEmail = "email@email.com";
		//user enters invalid input, then valid input
		inputs.add(invalidEmail);
		inputs.add(customerEmail);
		StringInputReader reader = ticketUI.getUserEmailReader();
		String output = reader.read("Enter email");
		assertEquals(customerEmail, output);
		assertEquals(2, terminal.getReadCalls());

	}
	
	@Test
	public void testMainMenuReader(){
		String invalidOption = "invalid";
		String option1 = "1";
		String option2 = "2";
		String option3 = "3";
		
		inputs.add(invalidOption);
		inputs.add(option1);
		inputs.add(option2);
		inputs.add(option3);
		
		EnumInputReader<MainMenu> reader = ticketUI.getMainMenuReader();
		
		MainMenu output1 = reader.read("");
		MainMenu output2 = reader.read("");
		MainMenu output3 = reader.read("");
		
		assertEquals(MainMenu.NUMBER_OF_SEATS_AVAILABLE, output1);
		assertEquals(MainMenu.SELECT_SEATS, output2);
		assertEquals(MainMenu.LOGOUT, output3);
		//account for the invalid input call
		assertEquals(4, terminal.getReadCalls());
		
	}
	
	@Test
	public void testConfirmationReader(){
		String invalidOption = "blah";
		String yes = "1";
		String no = "2";

		inputs.add(invalidOption);
		inputs.add(yes);
		inputs.add(no);
		
		EnumInputReader<YesNo> reader = ticketUI.getConfirmationReader();
		
		YesNo outputYes = reader.read("");
		YesNo outputNo = reader.read("");
		
		assertEquals(YesNo.YES, outputYes);
		assertEquals(YesNo.NO, outputNo);
		assertEquals(3, terminal.getReadCalls());
	}
	
	@Test
	public void testGetNumberOfSeatsReader(){
		String invalidOption = "not a number";
		String validNumber = "1234";
		
		inputs.add(invalidOption);
		inputs.add(validNumber);
		
		IntInputReader reader = ticketUI.getNumberOfSeatsRequestedReader();
		int output = reader.read("Enter email");
		assertEquals(1234, output);
		assertEquals(2, terminal.getReadCalls());
	}
	
	@Ignore
	@Test
	public void testBasicFlow() {
		TextIO textIO = TextIoFactory.getTextIO();
		
		TicketUserInterface app = new TicketUserInterface(textIO);
		List<String> inputs = terminal.getInputs();
		String outputs = terminal.getOutput();
		//login
		inputs.add("email@email.com");
		//discover number of seats
		inputs.add("1");
		//request seats
		inputs.add("2");
		//enter number of seats
		inputs.add("20");
		//confirm seat selection
		inputs.add("1");
		//logout
		inputs.add("3");
		//quit
		inputs.add("q");
		//accept all of the user input
		app.run();
		
	}

}
