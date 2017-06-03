package com.ticket.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class VenueTest {

	private String customerEmail = "email@email.com";

	@Test
	public void testAvailableSeatsInVenue() {
		Venue venue = new Venue(1, 10, 10);

		assertEquals(100, venue.numSeatsAvailable());
	}

	@Test
	public void testSeatHold() {
		int rows = 10;
		int seatsPerRow = 10;
		Venue venue = new Venue(1, rows, seatsPerRow);
		int totalVenueSeats = rows * seatsPerRow;
		int maxSeatsToHold = 10;
		int remainingSeats = totalVenueSeats;

		for (int i = 1; i <= maxSeatsToHold; i++) {
			venue.findAndHoldSeats(i, customerEmail);
			remainingSeats -= i;
			assertEquals(remainingSeats, venue.numSeatsAvailable());
			venue.printVenue();
		}

	}

}
