package com.ticket.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class VenueTest {

	@Test
	public void testAvailableSeatsInVenue() {
		Venue venue = new Venue(1, 10, 10);
		
		assertEquals(100, venue.numSeatsAvailable());
	}

}
