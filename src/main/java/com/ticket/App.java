package com.ticket;

import java.util.function.BiConsumer;

import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ticket.console.TicketUserInterface;

/**
 * The main Application class for the ticket service.
 * It offers the following capabilities.
 * 1. Find the number of seats available within a venue
 *		a. Seats that are neither held nor reserved are defined as available
 * 2. Find and hold the best available seats on behalf of the customer
 *		a. Each ticket hold should expire within a set number of seconds
 * 3. Reserve and commit a specific group of held seats for a customer
 * 
 * @author Grant Harper
 */
public class App {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(App.class);
	
	public static void main(String[] args) {
		LOGGER.info("Ticket Service starting up");
		TextIO textIO = TextIoFactory.getTextIO();
		
		BiConsumer<TextIO, String> app = new TicketUserInterface(textIO);
		
		app.accept(textIO, null);

	}
}
