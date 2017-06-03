package com.ticket.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class RowTest {

	private Row row;

	@Before
	public void setUp() {
		row = new Row(1, 1, 10);
	}

	@Test
	public void testHoldSeatsBasic() {
		Row row = new Row(1, 1, 10);
		// row is initially unreserved
		List<Seat> heldSeats1 = row.holdSeats(5);
		assertEquals(5, heldSeats1.size());
		assertEquals(1, heldSeats1.get(0).getSeatId());
		assertEquals(5, heldSeats1.get(4).getSeatId());
		System.out.println(row.print());

		// row now has reserved seats in it. remaining available seats would be
		// 7, 9 and 6, 8, 10
		List<Seat> heldSeats2 = row.holdSeats(3);
		assertEquals(3, heldSeats2.size());
		assertEquals(6, heldSeats2.get(0).getSeatId());
		assertEquals(10, heldSeats2.get(heldSeats2.size() - 1).getSeatId());
		System.out.println(row.print());

		// if we request more seats than are available in the row, returns null
		List<Seat> heldSeats3 = row.holdSeats(3);
		assertTrue(heldSeats3.isEmpty());

		// if we request 2 seats, we should get seats 7 and 9
		List<Seat> heldSeats4 = row.holdSeats(2);
		assertEquals(2, heldSeats4.size());
		assertEquals(7, heldSeats4.get(0).getSeatId());
		System.out.println(row.print());

	}

	@Test
	public void testHoldSeatsComplex() {
		Row row = new Row(1, 1, 10);

		assertTrue(row.holdSeats(11).isEmpty());

		assertEquals(5, row.holdSeats(5).size());
		assertTrue(row.holdSeats(6).isEmpty());

	}

	// @Ignore
	@Test
	public void testHoldSeatsTimed() throws InterruptedException {
		Row row = new Row(1, 1, 100);

		// hold 99 seats
		List<Seat> heldSeats = row.holdSeats(99);
		assertEquals(99, heldSeats.size());

		// hold 99 seats again
		List<Seat> holdSeatsDuringHold = row.holdSeats(99);
		assertTrue(holdSeatsDuringHold.isEmpty());

		// wait for hold to expire
		Thread.sleep(Venue.HOLD_DURATION.toMillis());

		// hold 99 seats after hold
		List<Seat> holdSeatsAfterHold = row.holdSeats(99);
		assertEquals(99, holdSeatsAfterHold.size());

	}

	@Test
	public void testGetRemainingSeats() throws InterruptedException {
		Row row = new Row(1, 1, 100);

		assertEquals(100, row.numSeatsAvailable());

		row.holdSeats(50);
		assertEquals(50, row.numSeatsAvailable());

		Thread.sleep(Venue.HOLD_DURATION.toMillis());

		assertEquals(100, row.numSeatsAvailable());
	}

}
