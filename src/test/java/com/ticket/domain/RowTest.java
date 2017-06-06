package com.ticket.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class RowTest {

	private Row smallRow;
	private Row bigRow;
	private Venue venue;
	private double seatHoldSeconds;
	private long holdExpireSleepMillis;

	@Before
	public void setUp() {
		seatHoldSeconds = 0.1;
		venue = new Venue(1, 10, 10, seatHoldSeconds);
		holdExpireSleepMillis = venue.getHoldDuration().toMillis() + 100;
		smallRow = new Row(1, 1, 10, venue);
		bigRow = new Row(1, 1, 100, venue);
	}
	
	
	@Test
	public void testHoldSeatsTooMany(){
		assertTrue(smallRow.holdSeats(11).isEmpty());
	}

	@Test
	public void testHoldSeatsBasic() {
		// row is initially unreserved
		List<Seat> heldSeats1 = smallRow.holdSeats(5);
		assertEquals(5, heldSeats1.size());
		assertEquals(1, heldSeats1.get(0).getSeatId());
		assertEquals(5, heldSeats1.get(4).getSeatId());
		System.out.println(smallRow.print());

		// row now has reserved seats in it. remaining available seats would be
		// 7, 9 and 6, 8, 10
		List<Seat> heldSeats2 = smallRow.holdSeats(3);
		assertEquals(3, heldSeats2.size());
		assertEquals(6, heldSeats2.get(0).getSeatId());
		assertEquals(10, heldSeats2.get(heldSeats2.size() - 1).getSeatId());
		System.out.println(smallRow.print());

		// if we request more seats than are available in the row, returns null
		List<Seat> heldSeats3 = smallRow.holdSeats(3);
		assertTrue(heldSeats3.isEmpty());

		// if we request 2 seats, we should get seats 7 and 9
		List<Seat> heldSeats4 = smallRow.holdSeats(2);
		assertEquals(2, heldSeats4.size());
		assertEquals(7, heldSeats4.get(0).getSeatId());
		System.out.println(smallRow.print());

	}

	@Test
	public void testHoldSeatsComplex() {

		assertTrue(smallRow.holdSeats(11).isEmpty());

		assertEquals(5, smallRow.holdSeats(5).size());
		assertTrue(smallRow.holdSeats(6).isEmpty());

	}

	// @Ignore
	@Test
	public void testHoldSeatsTimed() throws InterruptedException {

		// hold 99 seats
		List<Seat> heldSeats = bigRow.holdSeats(99);
		assertEquals(99, heldSeats.size());

		// hold 99 seats again
		List<Seat> holdSeatsDuringHold = bigRow.holdSeats(99);
		assertTrue(holdSeatsDuringHold.isEmpty());

		// wait for hold to expire
		Thread.sleep(holdExpireSleepMillis);

		// hold 99 seats after hold
		List<Seat> holdSeatsAfterHold = bigRow.holdSeats(99);
		assertEquals(99, holdSeatsAfterHold.size());

	}

	@Test
	public void testGetRemainingSeats() throws InterruptedException {

		assertEquals(100, bigRow.numSeatsAvailable());

		bigRow.holdSeats(50);
		assertEquals(50, bigRow.numSeatsAvailable());

		Thread.sleep(holdExpireSleepMillis);

		assertEquals(100, bigRow.numSeatsAvailable());
	}

}
