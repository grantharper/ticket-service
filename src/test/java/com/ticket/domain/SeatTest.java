package com.ticket.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

public class SeatTest {

	private Seat seat;
	private Venue venue;
	
	@Before
	public void setUp(){
		venue = new Venue(2);
		seat = new Seat(venue, "A", 1);
	}
	
	@Test
	public void testSeatHold() throws InterruptedException {
		assertNull(seat.getHoldTime());
		seat.placeHold();
		assertNotNull(seat.getHoldTime());
		Thread.sleep(2000);
		assertTrue(LocalDateTime.now().isAfter(seat.getHoldTime()));
	}
	
	@Test
	public void testIsHeld() throws InterruptedException{
		
		assertFalse(seat.isHeld());
		seat.placeHold();
		assertTrue(seat.isHeld());
		Thread.sleep(venue.getHoldDuration().toMillis());
		assertFalse(seat.isHeld());
		
	}

}
