package com.ticket.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ticket.App;
import com.ticket.Bootstrap;
import com.ticket.console.TicketUserInterface;
import com.ticket.domain.Row;
import com.ticket.domain.SeatHold;
import com.ticket.domain.SeatReservation;
import com.ticket.domain.Venue;
import com.ticket.repository.RowRepository;
import com.ticket.repository.SeatHoldRepository;
import com.ticket.repository.SeatRepository;
import com.ticket.repository.SeatReservationRepository;
import com.ticket.repository.VenueRepository;
import com.ticket.service.VenueTicketService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
public class VenueTicketServiceIntegrationTest {
	
	@MockBean
	private TicketUserInterface ticketUserInterface;
	
	@Autowired
	VenueTicketService venueTicketService;
	
	@Autowired
	Bootstrap bootstrap;
	
	@Autowired
	VenueRepository venueRepository;
	
	@Autowired
	SeatRepository seatRepository;
	
	@Autowired
	SeatHoldRepository seatHoldRepository;
	
	@Autowired
	RowRepository rowRepository;
	
	@Autowired
	SeatReservationRepository seatReservationRepository;

	private String customerEmail = "email@email.com";
	private Venue venue;
	private Integer totalVenueSeats;
	private long holdExpireSleepMillis;
	
	@Value("${venue.rows}")
	private Integer numRows;
	
	@Value("${venue.seatsPerRow}")
	private Integer numSeatsPerRow;
	
	@Value("${venue.id}")
	private Integer venueId;
	
	@Value("${venue.seatHoldSeconds}")
	private Integer seatHoldSeconds;
	
	@Before
	public void setUp(){

		venue = venueRepository.findOne(venueId);
		totalVenueSeats = numRows * numSeatsPerRow;
		holdExpireSleepMillis = (long) (seatHoldSeconds * 1000) + 100; //100 more milliseconds than the seat hold has
	}
	
	@After
	public void tearDown(){
		seatRepository.deleteAll();
		rowRepository.deleteAll();
		seatHoldRepository.deleteAll();
		seatReservationRepository.deleteAll();
		venueRepository.deleteAll();
		bootstrap.loadVenue();
	}
	
	@Test
	public void testNumSeatsAvailable() throws InterruptedException {
		//initial size of venue
		assertEquals(totalVenueSeats.intValue(), venueTicketService.numSeatsAvailable());
		//after a hold
		int numSeatsRequested = 10;
		venueTicketService.findAndHoldSeats(numSeatsRequested, customerEmail);
		assertEquals(totalVenueSeats - numSeatsRequested, venueTicketService.numSeatsAvailable());
		//after hold expires
		Thread.sleep(holdExpireSleepMillis);
		assertEquals(totalVenueSeats.intValue(), venueTicketService.numSeatsAvailable());
		//after a reservation
		SeatHold seatHold = venueTicketService.findAndHoldSeats(numSeatsRequested, customerEmail);
		venueTicketService.reserveSeats(seatHold.getSeatHoldId(), customerEmail);
		assertEquals(totalVenueSeats - numSeatsRequested, venueTicketService.numSeatsAvailable());
		
	}

	@Test
	public void testSimpleFindAndHoldSeats() {
		
		int maxSeatsToHold = 10;
		int remainingSeats = totalVenueSeats;
		//reserve 1, 2, 3 ... 10 seats
		for (int i = 1; i <= maxSeatsToHold; i++) {
			venueTicketService.findAndHoldSeats(i, customerEmail);
			remainingSeats -= i;
			assertEquals(remainingSeats, venueTicketService.numSeatsAvailable());
		}

	}
	
	@Test
	public void testLargeFindAndHoldSeats(){
		int reserveSeats = 84;
		venueTicketService.findAndHoldSeats(reserveSeats, customerEmail);
		assertEquals(totalVenueSeats - reserveSeats, venueTicketService.numSeatsAvailable());
		
	}
	
	@Test
	public void testTooManyFindAndHoldSeats(){
		int reserveSeats = totalVenueSeats + 1;
		assertNull(venueTicketService.findAndHoldSeats(reserveSeats, customerEmail));
		
	}
	
	@Test
	public void testFindRowEndsAndMaxOut(){
		int almostCompleteRowReservations = numSeatsPerRow - 2;
		
		for(int i = 0; i < numRows; i++){
			venueTicketService.findAndHoldSeats(almostCompleteRowReservations, customerEmail);
		}
		assertEquals(totalVenueSeats - almostCompleteRowReservations * numRows, venueTicketService.numSeatsAvailable());
		//after the venue is almost completely full request the remaining seats in one big request
		venueTicketService.findAndHoldSeats(totalVenueSeats - almostCompleteRowReservations * numRows, customerEmail);
		assertEquals(0, venueTicketService.numSeatsAvailable());
		
	}
	
	@Test
	public void testIncrementallyMaxOut(){
		int reserveSeats = 2;
		//reserve all seats by 2s
		for(int i = 0; i < totalVenueSeats / 2; i++){
			venueTicketService.findAndHoldSeats(reserveSeats, customerEmail);
		}
		assertEquals(totalVenueSeats % reserveSeats, venueTicketService.numSeatsAvailable());
		assertNull(venueTicketService.findAndHoldSeats(1, customerEmail));
		
	}
	
	@Test
	public void testFindAndHoldSeatsByThree(){
		int reserveSeats = 3;
		for(int i = 0; i < totalVenueSeats / reserveSeats; i++){
			venueTicketService.findAndHoldSeats(reserveSeats, customerEmail);
		}
		assertEquals(totalVenueSeats % reserveSeats, venueTicketService.numSeatsAvailable());
	}
	
	@Test
	public void testRequestMoreSeatsThanRowSize(){
		int reserveSeats = numSeatsPerRow + 1;
		venueTicketService.findAndHoldSeats(reserveSeats, customerEmail);
		assertEquals(totalVenueSeats - reserveSeats, venueTicketService.numSeatsAvailable());
		//there should be 0 seats left in the first row
		venue = venueRepository.findOne(venueId);
		List<Row> rowList = new ArrayList<>(venue.getRows());
		assertEquals(0, rowList.get(0).numSeatsAvailable());
		//there should be only 1 seat taken in the second row
		assertEquals(numSeatsPerRow - 1, rowList.get(1).numSeatsAvailable());
	}
	
	@Test
	public void testTimedSeatHolds() throws InterruptedException{
		int reserveSeats = 5;
		venueTicketService.findAndHoldSeats(reserveSeats, customerEmail);
		assertEquals(totalVenueSeats - reserveSeats, venueTicketService.numSeatsAvailable());
		Thread.sleep(holdExpireSleepMillis);
		//after hold expiration, all seats should be available
		assertEquals(totalVenueSeats.intValue(), venueTicketService.numSeatsAvailable());
	}
	
	@Test
	public void testReserveSeats(){
		int reserveSeats = 2;
		SeatHold seatHold = venueTicketService.findAndHoldSeats(reserveSeats, customerEmail);
		String confirmationCode = venueTicketService.reserveSeats(seatHold.getSeatHoldId(), customerEmail);
		
		SeatReservation reservation = seatReservationRepository.getReservationByConfirmationId(confirmationCode);
		assertEquals(reservation.getCustomerEmail(), customerEmail);
		assertEquals(reservation.getReservedSeats().size(), reserveSeats);
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
		assertEquals(venue.getRows().size() + 4, rowArray.length);
		
	}

}
