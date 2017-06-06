package com.ticket.domain;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class SeatHoldTest {

	private String customerEmail ;
	private LocalDateTime seatHoldExpiration;
	private SeatHold seatHold;

	@Before
	public void setUp(){
		customerEmail = "email@email.com";
		seatHoldExpiration = LocalDateTime.now().minusSeconds(1);
		seatHold = new SeatHold(new ArrayList<Seat>(), customerEmail, seatHoldExpiration);
	}

	@Test
	public void testSeatHoldExpired(){
		
		assertTrue(seatHold.isExpired());
		assertEquals("0 seconds", seatHold.secondsToExpiration());
		
		seatHold = new SeatHold(new ArrayList<Seat>(), customerEmail, LocalDateTime.now().plusNanos(2999999999L));
		assertEquals("2 seconds", seatHold.secondsToExpiration());
		
	}

}
