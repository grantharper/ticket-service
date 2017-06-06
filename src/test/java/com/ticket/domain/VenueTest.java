package com.ticket.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class VenueTest {

	private String customerEmail = "email@email.com";

	private Venue venue;
	private int venueId;
	private int venueRows;
	private int venueSeatsPerRow;
	private double venueHoldSeconds;
	private int totalVenueSeats;
	private long holdExpireSleepMillis;
	
	@Before
	public void setUp(){
		venueId = 1;
		venueRows = 10;
		venueSeatsPerRow = 20;
		venueHoldSeconds = 0.1;
		totalVenueSeats = venueRows * venueSeatsPerRow;
		holdExpireSleepMillis = (long) (venueHoldSeconds * 1000) + 100; //100 more milliseconds than the seat hold has
		venue = new Venue(venueId, venueRows, venueSeatsPerRow, venueHoldSeconds);
	}
	
	@Test
	public void testNumSeatsAvailable() throws InterruptedException {
		//initial size of venue
		assertEquals(totalVenueSeats, venue.numSeatsAvailable());
		//after a hold
		int numSeatsRequested = 10;
		venue.findAndHoldSeats(numSeatsRequested, customerEmail);
		assertEquals(totalVenueSeats - numSeatsRequested, venue.numSeatsAvailable());
		//after hold expires
		Thread.sleep(holdExpireSleepMillis);
		assertEquals(totalVenueSeats, venue.numSeatsAvailable());
		//after a reservation
		SeatHold seatHold = venue.findAndHoldSeats(numSeatsRequested, customerEmail);
		venue.reserveSeats(seatHold.getSeatHoldId(), customerEmail);
		assertEquals(totalVenueSeats - numSeatsRequested, venue.numSeatsAvailable());
		
	}

	@Test
	public void testSimpleFindAndHoldSeats() {
		
		int maxSeatsToHold = 10;
		int remainingSeats = totalVenueSeats;
		//reserve 1, 2, 3 ... 10 seats
		for (int i = 1; i <= maxSeatsToHold; i++) {
			venue.findAndHoldSeats(i, customerEmail);
			remainingSeats -= i;
			assertEquals(remainingSeats, venue.numSeatsAvailable());
		}

	}
	
	@Test
	public void testLargeFindAndHoldSeats(){
		int reserveSeats = 84;
		venue.findAndHoldSeats(reserveSeats, customerEmail);
		assertEquals(totalVenueSeats - reserveSeats, venue.numSeatsAvailable());
		
	}
	
	@Test
	public void testTooManyFindAndHoldSeats(){
		int reserveSeats = totalVenueSeats + 1;
		assertNull(venue.findAndHoldSeats(reserveSeats, customerEmail));
		
	}
	
	@Test
	public void testIncrementallyMaxOut(){
		int reserveSeats = 2;
		//reserve all seats by 2s
		for(int i = 0; i < totalVenueSeats / 2; i++){
			venue.findAndHoldSeats(reserveSeats, customerEmail);
		}
		assertEquals(totalVenueSeats % reserveSeats, venue.numSeatsAvailable());
		assertNull(venue.findAndHoldSeats(1, customerEmail));
		
	}
	
	@Test
	public void testFindAndHoldSeatsByThree(){
		int reserveSeats = 3;
		for(int i = 0; i < totalVenueSeats / reserveSeats; i++){
			venue.findAndHoldSeats(reserveSeats, customerEmail);
		}
		assertEquals(totalVenueSeats % reserveSeats, venue.numSeatsAvailable());
	}
	
	@Test
	public void testRequestMoreSeatsThanRowSize(){
		int reserveSeats = venueSeatsPerRow + 1;
		venue.findAndHoldSeats(reserveSeats, customerEmail);
		assertEquals(totalVenueSeats - reserveSeats, venue.numSeatsAvailable());
		//there should be 0 seats left in the first row
		assertEquals(0, venue.getRows().get(1).numSeatsAvailable());
		//there should be only 1 seat taken in the second row
		assertEquals(venueSeatsPerRow - 1, venue.getRows().get(2).numSeatsAvailable());
	}
	
	@Test
	public void testTimedSeatHolds() throws InterruptedException{
		int reserveSeats = 5;
		venue.findAndHoldSeats(reserveSeats, customerEmail);
		assertEquals(totalVenueSeats - reserveSeats, venue.numSeatsAvailable());
		Thread.sleep(holdExpireSleepMillis);
		//after hold expiration, all seats should be available
		assertEquals(totalVenueSeats, venue.numSeatsAvailable());
	}
	
	
	@Test
	public void testDivideGroups(){
		int numSeatsRequested = 47;
		List<Integer> seatRequests = venue.divideSeatRequestsIntoCompleteRows(numSeatsRequested);
		//47 would be divided into 2 groups of 20 (row size) and 1 group of 7
		assertEquals(3, seatRequests.size());
		assertEquals(20, seatRequests.get(0).intValue());
		assertEquals(7, seatRequests.get(2).intValue());
				
	}
	
	@Test
	public void testReserveSeats(){
		int reserveSeats = 2;
		SeatHold seatHold = venue.findAndHoldSeats(reserveSeats, customerEmail);
		String confirmationCode = venue.reserveSeats(seatHold.getSeatHoldId(), customerEmail);
		assertEquals(venue.getSeatReservations().get(confirmationCode).getCustomerEmail(), customerEmail);
		assertEquals(venue.getSeatReservations().get(confirmationCode).getReservedSeats().size(), reserveSeats);
	}
	
	@Test
	public void reserveSeatsFailsWithExpiredHold() throws InterruptedException{
		int reserveSeats = 2;
		SeatHold seatHold = venue.findAndHoldSeats(reserveSeats, customerEmail);
		Thread.sleep(holdExpireSleepMillis);
		String confirmationCode = venue.reserveSeats(seatHold.getSeatHoldId(), customerEmail);
		assertNull(confirmationCode);
	}
	
	@Test
	public void testPrintVenue(){
		String venueMap = venue.printVenue();
		String rowArray[] = venueMap.split("\n");
		//there are 4 more lines than rows in the message for readability
		assertEquals(venue.getNumRows() + 4, rowArray.length);
		
		//when venue is too large to be printed, display an error
		Venue largeVenue = new Venue(2, 100, 100, 40);
		String largeVenueMap = largeVenue.printVenue();
		assertEquals(Venue.SEAT_MAP_PRINT_ERROR_MSG, largeVenueMap);
		
	}

}
