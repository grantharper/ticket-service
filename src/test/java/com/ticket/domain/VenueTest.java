package com.ticket.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class VenueTest {

	private String customerEmail = "email@email.com";
	private Venue venue;
	private Integer venueRows;
	private Integer venueSeatsPerRow;
	private Integer venueHoldMilliseconds;
	private Integer totalVenueSeats;
	private Integer holdExpireSleepMillis;
	
	@Before
	public void setUp(){
		venue = new Venue(1);
		venueRows = 10;
		venueSeatsPerRow = 20;
		venueHoldMilliseconds = 100;
		createVenue();
		holdExpireSleepMillis = venueHoldMilliseconds + 100; //100 more milliseconds than the seat hold has
	}

	/**
	 * creates venue with POJOs instead of the database
	 */
	private void createVenue() {
		Set<Row> rows = new LinkedHashSet<>();
		for (Integer i = 0; i < venueRows; i++) {
			Row row = new Row(i + 1, venue);
			Set<Seat> seats = new LinkedHashSet<>();
			for (Integer j = 0; j < venueSeatsPerRow; j++) {
				Seat seat = new Seat(j + 1, row);
				seats.add(seat);
			}
			row.setSeats(seats);
			rows.add(row);
		}
		venue.setRows(rows);
		totalVenueSeats = venueRows * venueSeatsPerRow;
	}
	
	@Test
	public void testNumSeatsAvailable() throws InterruptedException {
		//initial size of venue
		assertEquals(totalVenueSeats.intValue(), venue.numSeatsAvailable());
		//after a hold
		int numSeatsRequested = 10;
		SeatHold seatHold = venue.findAndHoldSeats(numSeatsRequested, customerEmail);
		seatHold.commitSeatHold(LocalDateTime.now().plusNanos(venueHoldMilliseconds * 1000000));
		assertEquals(totalVenueSeats - numSeatsRequested, venue.numSeatsAvailable());
		//after hold expires
		Thread.sleep(holdExpireSleepMillis);
		assertEquals(totalVenueSeats.intValue(), venue.numSeatsAvailable());
		
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
	public void testFindRowEndsAndMaxOut(){
		int almostCompleteRowReservations = venueSeatsPerRow - 2;
		
		for(int i = 0; i < venueRows; i++){
			venue.findAndHoldSeats(almostCompleteRowReservations, customerEmail);
		}
		assertEquals(totalVenueSeats - almostCompleteRowReservations * venueRows, venue.numSeatsAvailable());
		//after the venue is almost completely full request the remaining seats in one big request
		venue.findAndHoldSeats(totalVenueSeats - almostCompleteRowReservations * venueRows, customerEmail);
		assertEquals(0, venue.numSeatsAvailable());
		
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
		List<Row> rows = new ArrayList(venue.getRows());
		assertEquals(0, rows.get(0).numSeatsAvailable());
		//there should be only 1 seat taken in the second row
		assertEquals(venueSeatsPerRow - 1, rows.get(1).numSeatsAvailable());
	}
	
	@Test
	public void testTimedSeatHolds() throws InterruptedException{
		int reserveSeats = 5;
		SeatHold seatHold = venue.findAndHoldSeats(reserveSeats, customerEmail);
		assertEquals(totalVenueSeats - reserveSeats, venue.numSeatsAvailable());
		seatHold.commitSeatHold(LocalDateTime.now().plusNanos(venueHoldMilliseconds * 1000000));
		Thread.sleep(holdExpireSleepMillis);
		//after hold expiration, all seats should be available
		assertEquals(totalVenueSeats.intValue(), venue.numSeatsAvailable());
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
	public void testPrintVenue(){
		String venueMap = venue.printVenue();
		String rowArray[] = venueMap.split("\n");
		//there are 4 more lines than rows in the message for readability
		assertEquals(venue.getRows().size() + 4, rowArray.length);
		
		//when venue is too large to be printed, display an error
		venueRows = 100;
		venueSeatsPerRow = 100;
		createVenue();
		
		String largeVenueMap = venue.printVenue();
		assertEquals(Venue.SEAT_MAP_PRINT_ERROR_MSG, largeVenueMap);
		
	}

}
