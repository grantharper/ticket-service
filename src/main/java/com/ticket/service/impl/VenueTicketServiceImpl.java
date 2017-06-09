package com.ticket.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ticket.domain.Row;
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
	
	@Autowired
	private VenueRepository venueRepository;
	
	@Autowired
	private SeatHoldRepository seatHoldRepository;
	
	@Autowired
	private SeatRepository seatRepository;
	
	@Autowired
	private SeatReservationRepository seatReservationRepository;
	
	@Value("${venue.seatHoldSeconds}")
	private Integer seatHoldSeconds;
	
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
		
		// case where there aren't enough remaining seats
		if (numSeatsRequested > numSeatsAvailable()) {
			LOGGER.info("Not enough remaining seats in the venue");
			return null;
		}

		SeatHold seatHold = new SeatHold(customerEmail, venue);
		// go through each row and get the List of seats that are held
		List<Seat> heldSeats = new ArrayList<>(numSeatsRequested);
		List<Integer> seatRequests = new ArrayList<>();
		
		//case where the number of seats requested is larger than the size of a complete row
		//divide the request in groups of complete rows 
		if(numSeatsRequested > getNumberOfSeatsPerRow(venue)){
			LOGGER.debug("Request is larger than the number of seats per row");
			seatRequests = divideSeatRequestsIntoCompleteRows(numSeatsRequested, venue);
		}
		//case where the number of seats requested is smaller than the size of a complete row
		else{
			LOGGER.debug("Request is smaller than the number of seats per row");
			seatRequests.add(numSeatsRequested);
		}

		//attempt to find seats while the list of located seats is smaller than the number requested
		while (heldSeats.size() < numSeatsRequested) {

			//loop through each divided up request and hold available seats
			for (int i = 0; i < seatRequests.size(); i++ ) {
				List<Seat> temp = holdSeats(seatRequests.get(i), seatHold, venue);
				if (!temp.isEmpty()) {
					//if seats were held, add them to the heldSeats list
					heldSeats.addAll(temp);
					//remove this request from the seatRequests
					seatRequests.remove(i);
					//exit if the total request has been fulfilled
					if (heldSeats.size() >= numSeatsRequested) {
						break;
					}
					//account for the removal of the request from the list by decrementing the counter
					i--;
				}
			}
			
			//if the seat requests could not all be fulfilled, divide the remaining requests by two and attempt to fulfill the smaller grouped requests
			List<Integer> updatedSeatRequests = new ArrayList<>();
			for (Integer request : seatRequests) {
				int halvedRequest = request / 2;
				if(halvedRequest < 1){
					//unlikely scenario for a multithreaded app where a seat request of 1 cannot be fulfilled so we attempt to divide it
					seatHold.invalidate();
					return null;
				}
				if ((request % 2) == 1) {
					updatedSeatRequests.add(halvedRequest + 1);
				} else {
					updatedSeatRequests.add(halvedRequest);
				}
				// add the potentially smaller request to the list last so
				// we find bigger groups together first
				updatedSeatRequests.add(halvedRequest);

			}
			seatRequests = updatedSeatRequests;

		}

		// populate the SeatHold with the list of seats and customer info and
		seatHold.commitSeatHold(LocalDateTime.now().plusSeconds(seatHoldSeconds));
		seatHoldRepository.save(seatHold);
		// add it to the list of venue seat holds
		for(Seat seat: heldSeats){
			seat.setSeatHold(seatHold);
			seatRepository.save(seat);
		}
		// return the seat hold
		return seatHold;

	}
	
	/**
	 * method to determine the number of seats per row in a venue. This assumes a rectangular shaped venue with even rows
	 * @param venue
	 * @return
	 */
	private int getNumberOfSeatsPerRow(Venue venue){
		return venue.getRows().iterator().next().getSeats().size();
	}

	/**
	 * loops through all the rows and sends back a seat hold list for the number
	 * of seats requested
	 * 
	 * @param seatsRequested
	 * @return held seats
	 */
	private List<Seat> holdSeats(int seatsRequested, SeatHold seatHold, Venue venue) {
		List<Seat> heldSeats = new ArrayList<>(seatsRequested);
		Iterator<Row> it = venue.getRows().iterator();

		while (it.hasNext() && heldSeats.isEmpty()) {
			Row currentRow = it.next();
			heldSeats = currentRow.holdSeats(seatsRequested, seatHold);
		}
		return heldSeats;
	}
	
	/**
	 * method used to divide requests into groups of complete rows and then remainder
	 * @param numSeatsRequested
	 * @return list of groupings of seats to be held
	 */
	List<Integer> divideSeatRequestsIntoCompleteRows(int numSeatsRequested, Venue venue){
		int numberOfCompleteRows = numSeatsRequested / getNumberOfSeatsPerRow(venue);
		int remainder = numSeatsRequested % getNumberOfSeatsPerRow(venue);
		
		List<Integer> seatRequests = new ArrayList<>(numberOfCompleteRows + 1);
		for(int i = 0; i < numberOfCompleteRows; i++){
			seatRequests.add(getNumberOfSeatsPerRow(venue));
		}
		if(remainder > 0){
			seatRequests.add(remainder);
		}
		return seatRequests;
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
		SeatReservation reservation = new SeatReservation(customerEmail, seatHold.getSeatsHeld());
		seatReservationRepository.save(reservation);
		
		// reserve the seats based on the seat hold
		for (Seat seat : seatHold.getSeatsHeld()) {
			seat.reserveSeat(customerEmail);
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
	}

	/**
	 * standard error message when the venue seat map is too large to display
	 */
	public static final String SEAT_MAP_PRINT_ERROR_MSG = "Seat map is too large to display. Only maps of 40x40 or smaller can be displayed.\n\n";
	
	/**
	 * prints a visual representation of the venue's rows and seats as well as their state
	 * A = available, H = held, R = reserved
	 */
	public String printVenue(Integer venueId) {
		Venue venue = venueRepository.findOne(venueId);
		//decide whether to print it to the console based on the size of the venue
		if(venue.getRows().size() > 40 || getNumberOfSeatsPerRow(venue) > 40){
			return SEAT_MAP_PRINT_ERROR_MSG;
		}
		Iterator<Row> it = venue.getRows().iterator();
		String venueModel = "";
		while (it.hasNext()) {
			venueModel += it.next().print() + "\n";
		}
		venueModel = "VENUE SEAT MAP \n\nA = Available, H = Held, R = Reserved \n\n" + venueModel + "\n\n";
		return venueModel;
	}

}
