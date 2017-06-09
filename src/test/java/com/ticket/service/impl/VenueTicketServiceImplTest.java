package com.ticket.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ticket.App;
import com.ticket.domain.SeatHold;
import com.ticket.domain.Venue;
import com.ticket.service.VenueTicketService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
public class VenueTicketServiceImplTest {
	
	@Autowired
	VenueTicketService venueTicketService;

	private String customerEmail = "email@email.com";
	private Venue venue;
	private Integer venueRows;
	private Integer venueSeatsPerRow;
	private double venueHoldSeconds;
	private Integer totalVenueSeats;
	private long holdExpireSleepMillis;
	
	@Before
	public void setUp(){
		venueRows = 10;
		venueSeatsPerRow = 20;
		venueHoldSeconds = 0.1;
		totalVenueSeats = venueRows * venueSeatsPerRow;
		holdExpireSleepMillis = (long) (venueHoldSeconds * 1000) + 100; //100 more milliseconds than the seat hold has
		venue = new Venue(1);
	}
	
	@Test
	public void testNumSeatsAvailable() throws InterruptedException {
		//initial size of venue
		assertEquals(totalVenueSeats.intValue(), venue.numSeatsAvailable());
		//after a hold
		int numSeatsRequested = 10;
		venueTicketService.findAndHoldSeats(numSeatsRequested, customerEmail);
		assertEquals(totalVenueSeats - numSeatsRequested, venue.numSeatsAvailable());
		//after hold expires
		Thread.sleep(holdExpireSleepMillis);
		assertEquals(totalVenueSeats, venue.numSeatsAvailable());
		//after a reservation
		SeatHold seatHold = venueTicketService.findAndHoldSeats(numSeatsRequested, customerEmail);
		venueTicketService.reserveSeats(seatHold.getSeatHoldId(), customerEmail);
		assertEquals(totalVenueSeats - numSeatsRequested, venue.numSeatsAvailable());
		
	}

	@Test
	public void testSimpleFindAndHoldSeats() {
		
		int maxSeatsToHold = 10;
		int remainingSeats = totalVenueSeats;
		//reserve 1, 2, 3 ... 10 seats
		for (int i = 1; i <= maxSeatsToHold; i++) {
			venueTicketService.findAndHoldSeats(i, customerEmail);
			remainingSeats -= i;
			assertEquals(remainingSeats, venue.numSeatsAvailable());
		}

	}
	
	@Test
	public void testLargeFindAndHoldSeats(){
		int reserveSeats = 84;
		venueTicketService.findAndHoldSeats(reserveSeats, customerEmail);
		assertEquals(totalVenueSeats - reserveSeats, venue.numSeatsAvailable());
		
	}
	
	@Test
	public void testTooManyFindAndHoldSeats(){
		int reserveSeats = totalVenueSeats + 1;
		assertNull(venueTicketService.findAndHoldSeats(reserveSeats, customerEmail));
		
	}
	
	@Test
	public void testFindRowEndsAndMaxOut(){
		int almostCompleteRowReservations = venueSeatsPerRow - 2;
		
		for(int i = 0; i < venueRows; i++){
			venueTicketService.findAndHoldSeats(almostCompleteRowReservations, customerEmail);
		}
		assertEquals(totalVenueSeats - almostCompleteRowReservations * venueRows, venue.numSeatsAvailable());
		//after the venue is almost completely full request the remaining seats in one big request
		venueTicketService.findAndHoldSeats(totalVenueSeats - almostCompleteRowReservations * venueRows, customerEmail);
		assertEquals(0, venue.numSeatsAvailable());
		
	}
	
	@Test
	public void testIncrementallyMaxOut(){
		int reserveSeats = 2;
		//reserve all seats by 2s
		for(int i = 0; i < totalVenueSeats / 2; i++){
			venueTicketService.findAndHoldSeats(reserveSeats, customerEmail);
		}
		assertEquals(totalVenueSeats % reserveSeats, venue.numSeatsAvailable());
		assertNull(venueTicketService.findAndHoldSeats(1, customerEmail));
		
	}
	
	@Test
	public void testFindAndHoldSeatsByThree(){
		int reserveSeats = 3;
		for(int i = 0; i < totalVenueSeats / reserveSeats; i++){
			venueTicketService.findAndHoldSeats(reserveSeats, customerEmail);
		}
		assertEquals(totalVenueSeats % reserveSeats, venue.numSeatsAvailable());
	}
	
	@Test
	public void testRequestMoreSeatsThanRowSize(){
		int reserveSeats = venueSeatsPerRow + 1;
		venueTicketService.findAndHoldSeats(reserveSeats, customerEmail);
		assertEquals(totalVenueSeats - reserveSeats, venue.numSeatsAvailable());
		//there should be 0 seats left in the first row
		assertEquals(0, venue.getRows().get(1).numSeatsAvailable());
		//there should be only 1 seat taken in the second row
		assertEquals(venueSeatsPerRow - 1, venue.getRows().get(2).numSeatsAvailable());
	}
	
	@Test
	public void testTimedSeatHolds() throws InterruptedException{
		int reserveSeats = 5;
		venueTicketService.findAndHoldSeats(reserveSeats, customerEmail);
		assertEquals(totalVenueSeats - reserveSeats, venue.numSeatsAvailable());
		Thread.sleep(holdExpireSleepMillis);
		//after hold expiration, all seats should be available
		assertEquals(totalVenueSeats, venue.numSeatsAvailable());
	}
	
	
//	@Test
//	public void testDivideGroups(){
//		int numSeatsRequested = 47;
//		List<Integer> seatRequests = venueTicketService.divideSeatRequestsIntoCompleteRows(numSeatsRequested, venue);
//		//47 would be divided into 2 groups of 20 (row size) and 1 group of 7
//		assertEquals(3, seatRequests.size());
//		assertEquals(20, seatRequests.get(0).intValue());
//		assertEquals(7, seatRequests.get(2).intValue());
//				
//	}
	
	@Test
	public void testReserveSeats(){
		int reserveSeats = 2;
		SeatHold seatHold = venueTicketService.findAndHoldSeats(reserveSeats, customerEmail);
		String confirmationCode = venueTicketService.reserveSeats(seatHold.getSeatHoldId(), customerEmail);
		//TODO: use the repository to create a custom query to look up by confirmation code
		assertEquals(venue.getSeatReservations().get(confirmationCode).getCustomerEmail(), customerEmail);
		assertEquals(venue.getSeatReservations().get(confirmationCode).getReservedSeats().size(), reserveSeats);
	}
	
	@Test
	public void reserveSeatsFailsWithExpiredHold() throws InterruptedException{
		int reserveSeats = 2;
		SeatHold seatHold = venueTicketService.findAndHoldSeats(reserveSeats, customerEmail);
		Thread.sleep(holdExpireSleepMillis);
		String confirmationCode = venueTicketService.reserveSeats(seatHold.getSeatHoldId(), customerEmail);
		assertNull(confirmationCode);
	}
	
	@Test
	public void testPrintVenue(){
		String venueMap = venueTicketService.printVenue(venueId);
		String rowArray[] = venueMap.split("\n");
		//there are 4 more lines than rows in the message for readability
		assertEquals(venue.getNumRows() + 4, rowArray.length);
		
		//when venue is too large to be printed, display an error
		Venue largeVenue = new Venue(2, 100, 100, 40);
		String largeVenueMap = largeVenue.printVenue();
		assertEquals(Venue.SEAT_MAP_PRINT_ERROR_MSG, largeVenueMap);
		
	}

}
