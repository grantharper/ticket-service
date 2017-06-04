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
	private String customerEmail = "email@email.com";

	@Before
	public void setUp() {
		seat = new Seat(1, 1, 1);
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
	public void testIsHeld() throws InterruptedException {

		assertTrue(seat.isAvailable());
		assertFalse(seat.isHeld());
		seat.placeHold();
		assertTrue(seat.isHeld());
		assertFalse(seat.isAvailable());
		Thread.sleep(Venue.HOLD_DURATION.toMillis());
		assertFalse(seat.isHeld());
		assertTrue(seat.isAvailable());

	}
	
	@Test
	public void testReservation(){
		seat.reserveSeat(customerEmail);
		assertTrue(seat.isReserved());
		assertFalse(seat.isAvailable());
	}

}
