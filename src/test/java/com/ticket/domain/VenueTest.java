package com.ticket.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

public class VenueTest {

	private String customerEmail = "email@email.com";

	private Venue venue;
	private int venueId;
	private int venueRows;
	private int venueSeatsPerRow;
	private int totalVenueSeats;
	private long holdExpireSleepMillis = Venue.HOLD_DURATION.toMillis() + 1000;
	
	@Before
	public void setUp(){
		venueId = 1;
		venueRows = 10;
		venueSeatsPerRow = 10;
		totalVenueSeats = venueRows * venueSeatsPerRow;
		venue = new Venue(venueId, venueRows, venueSeatsPerRow);
	}
	
	@Test
	public void testAvailableSeatsInVenue() {
		assertEquals(totalVenueSeats, venue.numSeatsAvailable());
	}

	@Test
	public void testSeatHold() {
		
		int maxSeatsToHold = 10;
		int remainingSeats = totalVenueSeats;

		for (int i = 1; i <= maxSeatsToHold; i++) {
			venue.findAndHoldSeats(i, customerEmail);
			remainingSeats -= i;
			assertEquals(remainingSeats, venue.numSeatsAvailable());
			venue.printVenue();
		}

	}
	
	@Test
	public void testBigSeatHold(){
		int reserveSeats = 84;
		venue.findAndHoldSeats(reserveSeats, customerEmail);
		assertEquals(totalVenueSeats - reserveSeats, venue.numSeatsAvailable());
		venue.printVenue();
		
	}
	
	@Test
	public void testMaxOutVenue(){
		int reserveSeats = 101;
		assertNull(venue.findAndHoldSeats(reserveSeats, customerEmail));
		
	}
	
	@Test
	public void testIncrementallyMaxOut(){
		int reserveSeats = 2;
		for(int i = 0; i < 50; i++){
			venue.findAndHoldSeats(reserveSeats, customerEmail);
		}
		assertEquals(0, venue.numSeatsAvailable());
		assertNull(venue.findAndHoldSeats(1, customerEmail));
		
	}
	
	@Test
	public void testReserveBy3(){
		int reserveSeats = 3;
		for(int i = 0; i < 33; i++){
			venue.findAndHoldSeats(reserveSeats, customerEmail);
		}
		assertEquals(1, venue.numSeatsAvailable());
	}
	
	@Test
	public void testRequestMoreSeatsThanInRows(){
		int reserveSeats = 11;
		venue.findAndHoldSeats(reserveSeats, customerEmail);
		assertEquals(totalVenueSeats - reserveSeats, venue.numSeatsAvailable());
		venue.printVenue();
	}
	
	@Test
	public void testTimedSeatHolds() throws InterruptedException{
		assertEquals(totalVenueSeats, venue.numSeatsAvailable());
		int reserveSeats = 5;
		venue.findAndHoldSeats(reserveSeats, customerEmail);
		assertEquals(totalVenueSeats - reserveSeats, venue.numSeatsAvailable());
		venue.printVenue();
		Thread.sleep(holdExpireSleepMillis);
		assertEquals(totalVenueSeats, venue.numSeatsAvailable());
		venue.printVenue();
	}
	
	@Test
	public void testSeatHoldObject(){
		int reserveSeats = 5;
		SeatHold seatHold = venue.findAndHoldSeats(reserveSeats, customerEmail);
		assertEquals(customerEmail, seatHold.getCustomerEmail());
		assertEquals(reserveSeats, seatHold.getSeatsHeld().size());
	}
	
	@Test
	public void reserveSeats(){
		int reserveSeats = 2;
		SeatHold seatHold = venue.findAndHoldSeats(reserveSeats, customerEmail);
		String confirmationCode = venue.reserveSeats(seatHold.getSeatHoldId(), customerEmail);
		assertEquals(venue.getSeatReservations().get(confirmationCode).getCustomerEmail(), customerEmail);
		assertEquals(venue.getSeatReservations().get(confirmationCode).getReservedSeats().size(), reserveSeats);
		venue.printVenue();
	}
	
	@Test
	public void reserveSeatsExpiredHold() throws InterruptedException{
		int reserveSeats = 2;
		SeatHold seatHold = venue.findAndHoldSeats(reserveSeats, customerEmail);
		Thread.sleep(holdExpireSleepMillis);
		String confirmationCode = venue.reserveSeats(seatHold.getSeatHoldId(), customerEmail);
		assertNull(confirmationCode);
	}

}
