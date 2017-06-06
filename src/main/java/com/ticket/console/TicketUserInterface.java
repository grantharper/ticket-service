package com.ticket.console;

import java.util.Map;
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
import com.ticket.service.VenueTicketService;
import com.util.AppProperties;

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
	 * the main menu reader used to allow the user to interact with the venue ticket service
	 */
	private EnumInputReader<MainMenu> mainMenuReader;
	
	/**
	 * the reader used to allow the user to log in with an email address
	 */
	private StringInputReader userEmailReader;
	
	/**
	 * the reader used to allow the user to request seats
	 */
	private IntInputReader numberOfSeatsRequestedReader;
	
	/**
	 * the reader used to allow the user to confirm their hold on seats
	 */
	private EnumInputReader<YesNo> confirmationReader;
	
	/**
	 * properties object to allow the user interface to access the property file information
	 */
	private Map<String, String> appProperties = new AppProperties().getProperties();
	
	/**
	 * the venue that the user will interact with
	 */
	private VenueTicketService venue;
	
	/**
	 * the terminal object used to issue prompts and read user input
	 */
	private TextTerminal terminal;
	
	/**
	 * properties to allow modification to terminal settings at run time
	 */
	private TerminalProperties props;
	
	/**
	 * instantiates the readers used by the terminal to interpret user input
	 * @param textIO
	 */
	public TicketUserInterface(TextIO textIO) {
		this.mainMenuReader = textIO.newEnumInputReader(MainMenu.class);
		this.userEmailReader = textIO.newStringInputReader().withPattern(EMAIL_REGEX);
		this.numberOfSeatsRequestedReader = textIO.newIntInputReader().withMinVal(0);
		this.confirmationReader = textIO.newEnumInputReader(YesNo.class);
		this.terminal = textIO.getTextTerminal();
		this.props = terminal.getProperties();
		loadVenue();
	}
	
	/**
	 * helper method to read the property file venue properties and instantiate the venue
	 */
	public void loadVenue(){
		try{
			int venueId = Integer.parseInt(appProperties.get("venue.id"));
			int venueRows = Integer.parseInt(appProperties.get("venue.rows"));
			int venueSeatsPerRow = Integer.parseInt(appProperties.get("venue.seatsPerRow"));
			int venueSeatHoldSeconds = Integer.parseInt(appProperties.get("venue.seatHoldSeconds"));
			venue = new Venue(venueId, venueRows, venueSeatsPerRow, venueSeatHoldSeconds);
		} catch(Exception e){
			LOGGER.error("Invalid properties found. Building simple venue of 10 rows, 20 seats per row, and a hold duration of 60 seconds");
			venue = new Venue(1, 10, 20, 60);
		}
		
	}

	/**
	 * method that runs the terminal that the user will use to provide inputs to the venue ticket service
	 */
	public void accept(TextIO textIO, String initData) {

		while (true) {
			terminal.println("Welcome to the Venue!\n Use ctrl-c to quit\n");
			printLineBreak();
			insertWaitTime();
			
			boolean customerLoggedIn = false;
			String customerEmail;
			int numberOfSeatsRequested;

			customerEmail = userEmailReader.read("Please provide your email address to log in: ");
			customerLoggedIn = true;

			insertWaitTime();
			changeToImportantColor();
			terminal.println("You have successfully logged in as " + customerEmail + "\n");
			resetPromptColor();
			insertWaitTime();
			
			while (customerLoggedIn) {
				
				printLineBreak();
				printVenueMap();
				terminal.println("Current User: " + customerEmail + "\n");
				MainMenu menu = mainMenuReader.read("Main Menu:");
				SeatHold seatHold = null;
				insertWaitTime();
				
				if (menu.equals(MainMenu.LOGOUT)) {
					insertWaitTime();
					changeToImportantColor();
					terminal.println("You have successfully logged out.");
					resetPromptColor();
					printLineBreak();
					break;
				} else if (menu.equals(MainMenu.NUMBER_OF_SEATS_AVAILABLE)) {

					int remainingSeats = venue.numSeatsAvailable();
					changeToImportantColor();
					terminal.printf("Remaining Seats: %d \n\n", remainingSeats);
					resetPromptColor();
					insertWaitTime();

				} else if(menu.equals(MainMenu.SELECT_SEATS)) {
					numberOfSeatsRequested = numberOfSeatsRequestedReader.read("Number of seats requested: ");

					if (numberOfSeatsRequested > 0) {
						
						terminal.printf("Calculating best available seats\n\n");
						insertWaitTime();
						
						

						seatHold = venue.findAndHoldSeats(numberOfSeatsRequested, customerEmail);
						changeToImportantColor();
						if(seatHold != null){
							terminal.printf("Held %d seats\n\n", numberOfSeatsRequested);
							terminal.print("Seat hold will expire in " + seatHold.secondsToExpiration() + ".\n\n");
						}else{
							terminal.println("Unable to hold seats. Not enough remaining seats in the venue");
						}
						resetPromptColor();
						printVenueMap();
						
						insertWaitTime();

						if (confirmationReader.read("Please confirm your selection: ").booleanValueOf()) {
							LOGGER.info("Confirming the seat selection");
							
							String confirmationNumber = venue.reserveSeats(seatHold.getSeatHoldId(), customerEmail);
							insertWaitTime();
							changeToImportantColor();
							if(confirmationNumber != null){
								terminal.printf("Success! Confirmation number: %s\n\n", confirmationNumber);
							}else{
								terminal.println("Your seat hold has expired. Please try again.");
							}
							resetPromptColor();
							insertWaitTime();
							
						}
					}
					
				}

			}

		}

	}
	
	private void insertWaitTime(){
		long waitTime = 200;
		try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void printVenueMap(){
		terminal.print(venue.printVenue());
	}
	
	private void resetPromptColor(){
		props.setPromptColor("white");
	}
	
	private void changeToImportantColor(){
		props.setPromptColor("red");
	}
	
	private void printLineBreak(){
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
