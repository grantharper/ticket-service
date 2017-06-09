package com.ticket;

import java.util.function.BiConsumer;

import javax.annotation.Resource;

import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.ticket.console.TicketUserInterface;

/**
 * The main Application class for the ticket service. It offers the following
 * capabilities. 1. Find the number of seats available within a venue a. Seats
 * that are neither held nor reserved are defined as available 2. Find and hold
 * the best available seats on behalf of the customer a. Each ticket hold should
 * expire within a set number of seconds 3. Reserve and commit a specific group
 * of held seats for a customer
 * 
 * @author Grant Harper
 */
@SpringBootApplication
public class App implements CommandLineRunner {

	public static final Logger LOGGER = LoggerFactory.getLogger(App.class);
	
	@Resource
	TicketUserInterface ticketUserInterface;

	/**
	 * main method for the application. Starts up the textIO terminal and allows
	 * for user interaction with the Ticket Service
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(App.class);
	}

	@Bean
	public TextIO textIO() {
		return TextIoFactory.getTextIO();
	}
//
//	@Bean
//	public TicketUserInterface ui() {
//		return new TicketUserInterface(textIO());
//	}
//
//	@Bean
//	public CommandLineRunner execute() {
//		return (args) -> {
//			LOGGER.info("Command line runner starting up");
//
//			BiConsumer<TextIO, String> app = ui();
//
//			app.accept(textIO(), null);
//
//		};
//	}

	@Override
	public void run(String... arg0) throws Exception {
		LOGGER.info("Command line runner starting up");

		BiConsumer<TextIO, String> app = ticketUserInterface;

		app.accept(textIO(), null);
	}
}
