package com.ticket.console;

import java.util.function.BiConsumer;

import org.beryx.textio.EnumInputReader;
import org.beryx.textio.IntInputReader;
import org.beryx.textio.StringInputReader;
import org.beryx.textio.TerminalProperties;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ticket.domain.SeatHold;
import com.ticket.domain.Venue;
import com.ticket.service.TicketService;

/**
 * The user interface class for the ticket service. This class will govern how
 * the user interacts with the ticket service The user will see a series of
 * console prompts
 */
public class TicketUserInterface implements BiConsumer<TextIO, String> {
	public static final Logger LOGGER = LoggerFactory.getLogger(TicketUserInterface.class);
	
	/**
	 * regex to validate user emails that are entered into the prompt
	 */
	private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
	
	/**
	 * welcome message for the venue
	 */
	private static final String WELCOME_MESSAGE = "Welcome to the Venue!";

	private EnumInputReader<MainMenu> mainMenuReader;
	private StringInputReader userEmailReader;
	private IntInputReader numberOfSeatsRequestedReader;
	private EnumInputReader<YesNo> confirmationReader;
	
	private TicketService venue;
	
	public TicketUserInterface(TextIO textIO) {
		mainMenuReader = textIO.newEnumInputReader(MainMenu.class);
		userEmailReader = textIO.newStringInputReader().withPattern(EMAIL_REGEX);
		numberOfSeatsRequestedReader = textIO.newIntInputReader().withMinVal(0);
		confirmationReader = textIO.newEnumInputReader(YesNo.class);
		venue = new Venue(1, 100, 200);
	}

	public void accept(TextIO textIO, String initData) {
		TextTerminal terminal = textIO.getTextTerminal();
		TerminalProperties props = terminal.getProperties();

		while (true) {
			terminal.println(WELCOME_MESSAGE);
			printLineBreak(terminal);
			insertWaitTime(terminal);
			
			boolean customerLoggedIn = false;
			String customerEmail;
			int numberOfSeatsRequested;

			customerEmail = userEmailReader.read("Please provide your email address to log in: ");
			customerLoggedIn = true;

			insertWaitTime(terminal);
			changeToImportantColor(props);
			terminal.println("You have successfully logged in as " + customerEmail + "\n");
			resetPromptColor(props);
			insertWaitTime(terminal);
			
			while (customerLoggedIn) {
				printLineBreak(terminal);
				terminal.println("Current User: " + customerEmail + "\n");
				MainMenu menu = mainMenuReader.read("Main Menu:");
				SeatHold seatHold = null;
				insertWaitTime(terminal);
				
				if (menu.equals(MainMenu.LOGOUT)) {
					insertWaitTime(terminal);
					changeToImportantColor(props);
					terminal.println("You have successfully logged out.");
					resetPromptColor(props);
					printLineBreak(terminal);
					break;
				} else if (menu.equals(MainMenu.SEATS_AVAILABLE)) {

					int remainingSeats = venue.numSeatsAvailable();
					changeToImportantColor(props);
					terminal.printf("Remaining Seats: %d \n\n", remainingSeats);
					resetPromptColor(props);
					insertWaitTime(terminal);

				} else if(menu.equals(MainMenu.SELECT_SEATS)) {
					numberOfSeatsRequested = numberOfSeatsRequestedReader.read("Number of seats requested: ");

					if (numberOfSeatsRequested > 0) {
						changeToImportantColor(props);
						terminal.printf("Calculating best available seats\n\n");
						insertWaitTime(terminal);

						seatHold = venue.findAndHoldSeats(numberOfSeatsRequested, customerEmail);
						
						if(seatHold != null){
							terminal.printf("Held %d seats\n\n", numberOfSeatsRequested);
							terminal.print("Seat hold will expire in " + (Venue.HOLD_DURATION.toMillis() / 1000) + " seconds.\n\n");
						}else{
							terminal.println("Unable to hold seats. Not enough remaining seats in the venue");
						}
						resetPromptColor(props);
						insertWaitTime(terminal);

						if (confirmationReader.read("Please confirm your selection: ").booleanValueOf()) {
							LOGGER.info("Confirming the seat selection");
							
							String confirmationNumber = venue.reserveSeats(seatHold.getSeatHoldId(), customerEmail);
							insertWaitTime(terminal);
							changeToImportantColor(props);
							if(confirmationNumber != null){
								terminal.printf("Success! Confirmation number: %s\n\n", confirmationNumber);
							}else{
								terminal.println("Your seat hold has expired. Please try again.");
							}
							resetPromptColor(props);
							insertWaitTime(terminal);
							
						}
					}
					
				}

			}

		}

	}
	
	private void insertWaitTime(TextTerminal terminal){
		long waitTime = 200;
		try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void resetPromptColor(TerminalProperties props){
		props.setPromptColor("white");
	}
	
	private void changeToImportantColor(TerminalProperties props){
		props.setPromptColor("red");
	}
	
	private void printLineBreak(TextTerminal terminal){
		terminal.println("------------------------------------------------\n");
	}

	EnumInputReader<MainMenu> getMainMenuReader() {
		return mainMenuReader;
	}

	StringInputReader getUserEmailReader() {
		return userEmailReader;
	}

	IntInputReader getNumberOfSeatsRequestedReader() {
		return numberOfSeatsRequestedReader;
	}

	EnumInputReader<YesNo> getConfirmationReader() {
		return confirmationReader;
	}
	
	
	

}
