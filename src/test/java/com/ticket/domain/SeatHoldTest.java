package com.ticket.domain;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class SeatHoldTest {

	private String customerEmail ;
	private SeatHold seatHold;
	private Venue venue;

	@Before
	public void setUp(){
		venue = new Venue(1, 10, 10, 0.001);
		customerEmail = "email@email.com";
		seatHold = new SeatHold(customerEmail, venue);
	}
	
	@Test
	public void testSeatHoldIsHolding() throws InterruptedException{
		assertTrue(seatHold.isHolding());
		seatHold.commitSeatHold(new ArrayList<Seat>());
		assertTrue(seatHold.isHolding());
		Thread.sleep(2);
		assertFalse(seatHold.isHolding());
		assertTrue(seatHold.isNotValid());
		
	}
	
	@Test
	public void testSeatHoldCommit(){
		assertNull(seatHold.getHoldExpiration());
		seatHold.commitSeatHold(new ArrayList<>());
		assertNotNull(seatHold.getHoldExpiration());
	}

	@Test
	public void testSeatHoldPrintSecondsToExpire(){
		seatHold.commitSeatHold(new ArrayList<Seat>());
		assertEquals("0 seconds", seatHold.printSecondsToExpiration());
		
		Venue venue2 = new Venue(1, 10, 10, 2.9);
		SeatHold seatHold2 = new SeatHold(customerEmail, venue2);
		seatHold2.commitSeatHold(new ArrayList<Seat>());
		assertEquals("2 seconds", seatHold2.printSecondsToExpiration());
		
	}
	
	@Test
	public void invalidateSeatHold(){
		assertTrue(seatHold.isHolding());
		seatHold.invalidate();
		assertFalse(seatHold.isHolding());
	}
	
	@Test
	public void invalidateCommittedSeatHold(){
		seatHold.commitSeatHold(new ArrayList<>());
		assertTrue(seatHold.isHolding());
		seatHold.invalidate();
		assertFalse(seatHold.isHolding());
	}

}
