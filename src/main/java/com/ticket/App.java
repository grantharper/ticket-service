package com.ticket;

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
	
	/**
	 * the user interface for the application
	 */
	@Resource
	TicketUserInterface ticketUserInterface;

	/**
	 * main method for the application. Starts up the textIO terminal and allows
	 * for user interaction with the Ticket Service
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(App.class).close();
	}

	/**
	 * bean for the textIO library that provides the terminal interface
	 * @return
	 */
	@Bean
	public TextIO textIO() {
		return TextIoFactory.getTextIO();
	}

	/**
	 * method that boots up the console application
	 */
	@Override
	public void run(String... arg0) throws Exception {
		LOGGER.info("Command line runner starting up");

		ticketUserInterface.run();
	}
}
