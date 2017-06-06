package com.ticket.domain;

import static org.junit.Assert.assertEquals;
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
	private Venue venue;
	private Row row;
	private double seatHoldSeconds;
	private long holdExpireSleepMillis;
	

	@Before
	public void setUp() {
		seatHoldSeconds = 0.1;
		venue = new Venue(1, 10, 10, seatHoldSeconds);
		row = new Row(1, 10, venue);
		holdExpireSleepMillis = venue.getHoldDuration().toMillis() + 100;
		seat = new Seat(1, venue, row);
	}

	@Test
	public void testSeatHold() throws InterruptedException {
		assertNull(seat.getHoldTime());
		seat.placeHold();
		assertNotNull(seat.getHoldTime());
		Thread.sleep(holdExpireSleepMillis);
		assertTrue(LocalDateTime.now().isAfter(seat.getHoldTime()));
	}

	@Test
	public void testIsHeld() throws InterruptedException {

		assertTrue(seat.isAvailable());
		assertFalse(seat.isHeld());
		seat.placeHold();
		assertTrue(seat.isHeld());
		assertFalse(seat.isAvailable());
		Thread.sleep(holdExpireSleepMillis);
		assertFalse(seat.isHeld());
		assertTrue(seat.isAvailable());

	}
	
	@Test
	public void testReservation(){
		seat.reserveSeat(customerEmail);
		assertTrue(seat.isReserved());
		assertFalse(seat.isAvailable());
	}
	
	@Test
	public void testPrintSeat(){
		assertEquals(Seat.SEAT_AVAILABLE_CODE, seat.print());
		seat.placeHold();
		assertEquals(Seat.SEAT_HELD_CODE, seat.print());
		seat.reserveSeat(customerEmail);
		assertEquals(Seat.SEAT_RESERVED_CODE, seat.print());
	}

}
