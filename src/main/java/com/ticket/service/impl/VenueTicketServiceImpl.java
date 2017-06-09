package com.ticket.service.impl;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ticket.domain.Seat;
import com.ticket.domain.SeatHold;
import com.ticket.domain.SeatReservation;
import com.ticket.domain.Venue;
import com.ticket.repository.SeatHoldRepository;
import com.ticket.repository.SeatRepository;
import com.ticket.repository.SeatReservationRepository;
import com.ticket.repository.VenueRepository;
import com.ticket.service.VenueTicketService;

@Service
public class VenueTicketServiceImpl implements VenueTicketService{
	
	public static final Logger LOGGER = LoggerFactory.getLogger(VenueTicketService.class);
	
	/**
	 * data repository for the venue
	 */
	@Autowired
	private VenueRepository venueRepository;
	
	/**
	 * data repository for the seat holds
	 */
	@Autowired
	private SeatHoldRepository seatHoldRepository;
	
	/**
	 * data repository for the seats
	 */
	@Autowired
	private SeatRepository seatRepository;
	
	/**
	 * data repository for the seat reservations
	 */
	@Autowired
	private SeatReservationRepository seatReservationRepository;
	
	/**
	 * the number of seconds for which a seat hold will apply
	 */
	@Value("${venue.seatHoldSeconds}")
	private Integer seatHoldSeconds;
	
	/**
	 * the id for the venue used in this application
	 */
	@Value("${venue.id}")
	private Integer venueId;
	
	public VenueTicketServiceImpl(){
	}
	
	/**
	 * see TicketService for method summary
	 */
	@Override
	public int numSeatsAvailable() {
		LOGGER.info("Retrieving number of available seats in the venue");
		Venue venue = venueRepository.findOne(venueId);
		return venue.numSeatsAvailable();
	}

	/**
	 * see TicketService for method summary
	 */
	@Override
	public SeatHold findAndHoldSeats(int numSeatsRequested, String customerEmail) {
		LOGGER.info("Attempting to find and hold " + numSeatsRequested + " seats in the venue");
		Venue venue = venueRepository.findOne(venueId);
		
		SeatHold seatHold = venue.findAndHoldSeats(numSeatsRequested, customerEmail);
		
		if(seatHold == null){
			return null;
		}
		// populate the SeatHold with the list of seats and customer info and
		seatHold.commitSeatHold(LocalDateTime.now().plusSeconds(seatHoldSeconds));
		seatHoldRepository.save(seatHold);
		// add it to the list of venue seat holds
		for(Seat seat: seatHold.getSeatsHeld()){
			seat.setSeatHold(seatHold);
			seatRepository.save(seat);
		}
		// return the seat hold
		return seatHold;

	}

	/**
	 * see TicketService for method summary
	 */
	@Override
	public String reserveSeats(int seatHoldId, String customerEmail) {
		SeatHold seatHold = seatHoldRepository.findOne(seatHoldId);
		
		//determine if the seatHold is no longer valid
		if(!seatHold.isHolding()){
			return null;
		}
		SeatReservation reservation = new SeatReservation(customerEmail);
		seatReservationRepository.save(reservation);
		
		// reserve the seats based on the seat hold
		for (Seat seat : seatHold.getSeatsHeld()) {
			seat.reserveSeat(reservation);
			seatRepository.save(seat);
		}
		// return confirmation Id and populate it in a list for later retrieval
		// if necessary
		
		return reservation.getConfirmationId();
	}
	
	/**
	 * see VenueTicketService for method summary
	 */
	@Override
	public void invalidateHold(SeatHold seatHold) {
		seatHold.invalidate();
		seatHoldRepository.save(seatHold);
	}

	
	/**
	 * prints a visual representation of the venue's rows and seats as well as their state
	 * A = available, H = held, R = reserved
	 */
	public String printVenue(Integer venueId) {
		Venue venue = venueRepository.findOne(venueId);
		return venue.printVenue();
	}

	

}
