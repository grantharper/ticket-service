package com.ticket.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

public class SeatHoldTest {

	private String customerEmail ;
	private SeatHold seatHold;
	private Venue venue;
	private long setExpireNanos;
	private long holdExpireSleepMillis;

	@Before
	public void setUp(){
		venue = new Venue(1);
		setExpireNanos = 100 * 1_000_000;
		holdExpireSleepMillis = 200;
		customerEmail = "email@email.com";
		seatHold = new SeatHold(customerEmail, venue);
	}
	
	@Test
	public void testSeatHoldIsHolding() throws InterruptedException{
		assertTrue(seatHold.isHolding());
		seatHold.commitSeatHold(LocalDateTime.now().plusNanos(setExpireNanos));
		assertTrue(seatHold.isHolding());
		Thread.sleep(holdExpireSleepMillis);
		assertFalse(seatHold.isHolding());
		
	}
	
	@Test
	public void testSeatHoldCommit(){
		assertNull(seatHold.getExpireTime());
		LocalDateTime expireTime = LocalDateTime.now().plusNanos(setExpireNanos);
		seatHold.commitSeatHold(expireTime);
		assertNotNull(seatHold.getExpireTime());
		assertEquals(seatHold.getExpireTime(), expireTime);
	}

	@Test
	public void testSeatHoldPrintSecondsToExpire(){
		LocalDateTime expireTime = LocalDateTime.now().plusNanos(setExpireNanos);
		seatHold.commitSeatHold(expireTime);
		assertEquals("0 seconds", seatHold.printSecondsToExpiration());
		LocalDateTime expireTime2 = LocalDateTime.now().plusNanos(21 * 100000000);
		seatHold.commitSeatHold(expireTime2);
		assertEquals("2 seconds", seatHold.printSecondsToExpiration());
		
	}
	
	@Test
	public void invalidateSeatHold(){
		assertTrue(seatHold.isHolding());
		seatHold.invalidate();
		assertFalse(seatHold.isHolding());
	}
	
	@Test
	public void invalidateCommittedSeatHold(){
		LocalDateTime expireTime = LocalDateTime.now().plusNanos(setExpireNanos);
		seatHold.commitSeatHold(expireTime);
		assertTrue(seatHold.isHolding());
		seatHold.invalidate();
		assertFalse(seatHold.isHolding());
	}

}
