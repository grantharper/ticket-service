package com.ticket.console;

import java.util.function.BiConsumer;

import org.beryx.textio.EnumInputReader;
import org.beryx.textio.IntInputReader;
import org.beryx.textio.StringInputReader;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The user interface class for the ticket service. This class will govern how
 * the user interacts with the ticket service The user will see a series of
 * console prompts
 */
public class TicketUserInterface implements BiConsumer<TextIO, String> {

	public static final Logger LOGGER = LoggerFactory.getLogger(TicketUserInterface.class);
	private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
	private static final String WELCOME_MESSAGE = "Welcome to the Venue!\n\nEnter your email to login and select your tickets\n";

	private EnumInputReader<MainMenu> mainMenuReader;
	private StringInputReader userEmailReader;
	private IntInputReader numberOfSeatsRequestedReader;
	private EnumInputReader<YesNo> confirmationReader;
	
	public TicketUserInterface(TextIO textIO) {
		mainMenuReader = textIO.newEnumInputReader(MainMenu.class);
		userEmailReader = textIO.newStringInputReader().withPattern(EMAIL_REGEX);
		numberOfSeatsRequestedReader = textIO.newIntInputReader().withMinVal(0);
		confirmationReader = textIO.newEnumInputReader(YesNo.class);
	}

	public void accept(TextIO textIO, String initData) {
		TextTerminal terminal = textIO.getTextTerminal();

		terminal.println(WELCOME_MESSAGE);

		while (true) {
			boolean userLoggedIn = false;
			String userEmail;
			int numberOfSeatsRequested;

			userEmail = userEmailReader.read("Email: ");
			userLoggedIn = true;

			while (userLoggedIn) {
				terminal.println("User email " + userEmail + " is logged in");
				MainMenu menu = mainMenuReader.read("Main Menu:");
				
				if (menu.equals(MainMenu.LOGOUT)) {
					terminal.println("Logging out");
					break;
				} else if (menu.equals(MainMenu.SEATS_AVAILABLE)) {

					// TODO:calculate the number of remaining seats in the venue

					int remainingSeats = 0;
					terminal.printf("Remaining Seats: %d \n\n", remainingSeats);

				} else if(menu.equals(MainMenu.SELECT_SEATS)) {
					numberOfSeatsRequested = numberOfSeatsRequestedReader.read("Number of seats requested: ");

					if (numberOfSeatsRequested > 0) {

						terminal.printf("Calculating best available seats\n\n");
						// TODO:perform operations to hold the seats

						terminal.printf("Held %d seats\n\n", numberOfSeatsRequested);

						if (confirmationReader.read("Please confirm your selection: ").booleanValueOf()) {
							// TODO:perform operations to secure the seats
							LOGGER.info("Confirming the seat selection");
							String confirmationNumber = "XXXXX";
							terminal.printf("Success! Confirmation number: %s\n\n", confirmationNumber);
						}
					}
					
				}

			}

		}

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
