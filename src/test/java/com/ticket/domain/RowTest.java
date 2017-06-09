package com.ticket.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class RowTest {

	private Row smallRow;
	private Row bigRow;
	private String customerEmail = "email@email.com";
	private Venue venue;
	private SeatHold seatHold;
	private long holdExpireSleepMillis;

	@Before
	public void setUp() {
		holdExpireSleepMillis = 200;
		venue = new Venue(1);
		seatHold = new SeatHold(customerEmail, venue);
		smallRow = new Row(1, venue);
		Set<Seat> seats = new LinkedHashSet<>();
		for(int i = 0; i < 10; i++){
			seats.add(new Seat(i + 1, smallRow));
		}
		smallRow.setSeats(seats);
		
		bigRow = new Row(1, venue);
		Set<Seat> bigRowSeats = new LinkedHashSet<>();
		for(int i = 0; i < 100; i++){
			bigRowSeats.add(new Seat(i + 1, bigRow));
		}
		
		bigRow.setSeats(bigRowSeats);
		
	}
	
	
	@Test
	public void testHoldSeatsTooMany(){
		assertTrue(smallRow.holdSeats(11, seatHold).isEmpty());
	}

	@Test
	public void testHoldSeatsBasic() {
		// row is initially unreserved
		List<Seat> heldSeats1 = smallRow.holdSeats(5, seatHold);
		assertEquals(5, heldSeats1.size());
		assertEquals(1, heldSeats1.get(0).getSeatNumber().longValue());
		assertEquals(5, heldSeats1.get(4).getSeatNumber().longValue());
		System.out.println(smallRow.print());

		// row now has reserved seats in it. remaining available seats would be
		// 7, 9 and 6, 8, 10
		List<Seat> heldSeats2 = smallRow.holdSeats(3, seatHold);
		assertEquals(3, heldSeats2.size());
		assertEquals(6, heldSeats2.get(0).getSeatNumber().longValue());
		assertEquals(10, heldSeats2.get(heldSeats2.size() - 1).getSeatNumber().longValue());
		System.out.println(smallRow.print());

		// if we request more seats than are available in the row, returns null
		List<Seat> heldSeats3 = smallRow.holdSeats(3, seatHold);
		assertTrue(heldSeats3.isEmpty());

		// if we request 2 seats, we should get seats 7 and 9
		List<Seat> heldSeats4 = smallRow.holdSeats(2, seatHold);
		assertEquals(2, heldSeats4.size());
		assertEquals(7, heldSeats4.get(0).getSeatNumber().longValue());
		System.out.println(smallRow.print());

	}

	@Test
	public void testHoldSeatsComplex() {

		assertTrue(smallRow.holdSeats(11, seatHold).isEmpty());

		assertEquals(5, smallRow.holdSeats(5, seatHold).size());
		assertTrue(smallRow.holdSeats(6, seatHold).isEmpty());

	}

	// @Ignore
	@Test
	public void testHoldSeatsTimed() throws InterruptedException {

		// hold 99 seats
		List<Seat> heldSeats = bigRow.holdSeats(99, seatHold);
		assertEquals(99, heldSeats.size());

		// hold 99 seats again
		List<Seat> holdSeatsDuringHold = bigRow.holdSeats(99, seatHold);
		assertTrue(holdSeatsDuringHold.isEmpty());
		seatHold.commitSeatHold(LocalDateTime.now().plusNanos(holdExpireSleepMillis * 1000000));
		// wait for hold to expire
		Thread.sleep(holdExpireSleepMillis);

		// hold 99 seats after hold
		List<Seat> holdSeatsAfterHold = bigRow.holdSeats(99, seatHold);
		assertEquals(99, holdSeatsAfterHold.size());

	}

	@Test
	public void testGetRemainingSeats() throws InterruptedException {

		assertEquals(100, bigRow.numSeatsAvailable());

		bigRow.holdSeats(50, seatHold);
		seatHold.commitSeatHold(LocalDateTime.now().plusNanos(holdExpireSleepMillis * 1000000));
		assertEquals(50, bigRow.numSeatsAvailable());

		Thread.sleep(holdExpireSleepMillis);

		assertEquals(100, bigRow.numSeatsAvailable());
	}
	
	@Test
	public void testPrintRow(){
		String result = smallRow.print();
		//size of row is twice size of number of seats plus a space in the middle
		assertEquals(smallRow.getSeats().size() * 2 + 1, result.length());
	}

}
