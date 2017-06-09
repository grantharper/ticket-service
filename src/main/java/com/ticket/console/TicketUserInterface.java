package com.ticket.console;

import org.beryx.textio.EnumInputReader;
import org.beryx.textio.IntInputReader;
import org.beryx.textio.StringInputReader;
import org.beryx.textio.TerminalProperties;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ticket.domain.SeatHold;
import com.ticket.service.VenueTicketService;

/**
 * The user interface class for the ticket service. This class will govern how
 * the user interacts with the ticket service The user will see a series of
 * console prompts
 */
@Component
public class TicketUserInterface {
	public static final Logger LOGGER = LoggerFactory.getLogger(TicketUserInterface.class);
	
	/**
	 * regex to validate user emails that are entered into the prompt
	 */
	private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

	/**
	 * regex to validate initial user input including the q quit command
	 */
	private static final String LOGIN_REGEX = "(^[q]$)|(" + EMAIL_REGEX + ")";
	
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
	 * the venue that the user will interact with
	 */
	@Autowired
	private VenueTicketService venueTicketService;
	
	/**
	 * the terminal object used to issue prompts and read user input
	 */
	private TextTerminal terminal;
	
	/**
	 * properties to allow modification to terminal settings at run time
	 */
	private TerminalProperties props;
	
	/**
	 * flag indicating whether the venue map should be displayed to the user
	 */
	@Value("${venue.displayMap}")
	private boolean displayVenueMap;
	
	/**
	 * id of the only venue currently supported in this application
	 */
	@Value("${venue.id}")
	private Integer venueId;
	
	/**
	 * instantiates the readers used by the terminal to interpret user input
	 * @param textIO
	 */
	public TicketUserInterface(TextIO textIO) {
		this.mainMenuReader = textIO.newEnumInputReader(MainMenu.class);
		this.userEmailReader = textIO.newStringInputReader().withPattern(LOGIN_REGEX);
		this.numberOfSeatsRequestedReader = textIO.newIntInputReader().withMinVal(0);
		this.confirmationReader = textIO.newEnumInputReader(YesNo.class);
		this.terminal = textIO.getTextTerminal();
		this.props = terminal.getProperties();
	}

	/**
	 * method that runs the terminal that the user will use to provide inputs to the venue ticket service
	 */
	public void run() {

		LOGGER.info("Accepting user input via the terminal");
		while (true) {
			terminal.println("Welcome to the Venue!");
			printLineBreak();
			insertWaitTime();
			
			boolean customerLoggedIn = false;
			String customerEmail;
			int numberOfSeatsRequested;

			customerEmail = userEmailReader.read("Please provide your email address to log in or enter \"q\" to quit: \n");
			if(customerEmail.equals("q")){
				LOGGER.info("Customer provided quit command. Exiting");
				terminal.println("Exited Application");
				terminal.dispose();
				return;
				//break;
			}
			customerLoggedIn = true;
			LOGGER.info("Customer with email " + customerEmail + " has logged in");

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
					LOGGER.info("Customer " + customerEmail + " selected Logout");
					insertWaitTime();
					changeToImportantColor();
					terminal.println("You have successfully logged out.");
					resetPromptColor();
					printLineBreak();
					break;
				} else if (menu.equals(MainMenu.NUMBER_OF_SEATS_AVAILABLE)) {
					LOGGER.info("Customer " + customerEmail + " selected Number of Seats Available");
					int remainingSeats = venueTicketService.numSeatsAvailable();
					changeToImportantColor();
					terminal.printf("Remaining Seats: %d \n\n", remainingSeats);
					resetPromptColor();
					insertWaitTime();

				} else if(menu.equals(MainMenu.SELECT_SEATS)) {
					LOGGER.info("Customer " + customerEmail + " selected Select Seats");
					numberOfSeatsRequested = numberOfSeatsRequestedReader.read("Number of seats requested: ");

					if (numberOfSeatsRequested > 0) {
						
						terminal.printf("Calculating best available seats\n\n");
						insertWaitTime();

						seatHold = venueTicketService.findAndHoldSeats(numberOfSeatsRequested, customerEmail);
						
						changeToImportantColor();
						if(seatHold != null){
							LOGGER.info("Customer " + customerEmail + " has held " + numberOfSeatsRequested + " seats");
							terminal.printf("Held %d seats\n\n", numberOfSeatsRequested);
							terminal.print("Seat hold will expire in " + seatHold.printSecondsToExpiration() + ".\n\n");
						}else{
							LOGGER.info("Unable to hold seats due to not enough remaining");
							terminal.println("Unable to hold seats. Not enough remaining seats in the venue");
						}
						resetPromptColor();
						printVenueMap();
						
						insertWaitTime();

						if (confirmationReader.read("Please confirm your selection: ").booleanValueOf()) {
							LOGGER.info("Confirming the seat selection");
							
							String confirmationNumber = venueTicketService.reserveSeats(seatHold.getSeatHoldId(), customerEmail);
							insertWaitTime();
							changeToImportantColor();
							if(confirmationNumber != null){
								LOGGER.info("Seat Selection confirmed");
								terminal.printf("Success! Confirmation number: %s\n\n", confirmationNumber);
							}else{
								LOGGER.info("Seat Selection reservation failed");
								terminal.println("Your seat hold has expired. Please try again.");
							}
							resetPromptColor();
							insertWaitTime();
							
						}
						//selection of no when asked to confirm hold
						else{
							LOGGER.info("Remove seat hold because customer indicated they did not want to reserve the seats");
							venueTicketService.invalidateHold(seatHold);
						}
					}
					
				}

			}

		}

	}
	
	/**
	 * helper method to insert wait time in the terminal for more intuitive user experience
	 */
	private void insertWaitTime(){
		long waitTime = 200;
		try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * helper method to print the venue map to the console
	 */
	private void printVenueMap(){
		if(displayVenueMap){
			String venueMap = venueTicketService.printVenue(venueId);
			LOGGER.debug(venueMap);
			terminal.print(venueMap);
		}
	}
	
	/**
	 * helper method to reset the prompt color back to the normal color
	 */
	private void resetPromptColor(){
		props.setPromptColor("white");
	}
	
	/**
	 * helper method to change the prompt color to red to signify important output
	 */
	private void changeToImportantColor(){
		props.setPromptColor("red");
	}
	
	/**
	 * helper method to print a line break in the terminal
	 */
	private void printLineBreak(){
		terminal.println("------------------------------------------------\n");
	}

	/**
	 * @return the logger
	 */
	public static Logger getLogger() {
		return LOGGER;
	}

	/**
	 * @return the emailRegex
	 */
	public static String getEmailRegex() {
		return EMAIL_REGEX;
	}

	/**
	 * @return the mainMenuReader
	 */
	public EnumInputReader<MainMenu> getMainMenuReader() {
		return mainMenuReader;
	}

	/**
	 * @return the userEmailReader
	 */
	public StringInputReader getUserEmailReader() {
		return userEmailReader;
	}

	/**
	 * @return the numberOfSeatsRequestedReader
	 */
	public IntInputReader getNumberOfSeatsRequestedReader() {
		return numberOfSeatsRequestedReader;
	}

	/**
	 * @return the confirmationReader
	 */
	public EnumInputReader<YesNo> getConfirmationReader() {
		return confirmationReader;
	}

	/**
	 * @return the terminal
	 */
	public TextTerminal getTerminal() {
		return terminal;
	}

	/**
	 * @return the props
	 */
	public TerminalProperties getProps() {
		return props;
	}
	

}
