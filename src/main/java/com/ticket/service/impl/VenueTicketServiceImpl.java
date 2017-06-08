package com.ticket.service.impl;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ticket.domain.Row;
import com.ticket.domain.Seat;
import com.ticket.domain.SeatHold;
import com.ticket.domain.SeatReservation;
import com.ticket.domain.Venue;
import com.ticket.service.VenueTicketService;

public class VenueTicketServiceImpl implements VenueTicketService{
	
	public static final Logger LOGGER = LoggerFactory.getLogger(VenueTicketService.class);
	
	private Venue venue;
	
	private Duration holdDuration;
	
	public VenueTicketServiceImpl(final double seatHoldSeconds, final Long numSeatsPerRow, final Long numRows){
		this.numSeatsPerRow = numSeatsPerRow;
		this.numRows = numRows;
		this.holdDuration = Duration.of(Math.round(seatHoldSeconds * 1000), ChronoUnit.MILLIS);
		this.venue = new Venue(numRows, numSeatsPerRow);
	}
	
	//TODO: take these out since the DB should be queried for this info
	
	/**
	 * number of rows in the venue
	 */
	private final Long numRows;
	
	/**
	 * number of seats per row in the venue
	 */
	private final Long numSeatsPerRow;
	
	/**
	 * see TicketService for method summary
	 */
	@Override
	public int numSeatsAvailable() {
		LOGGER.info("Retrieving number of available seats in the venue");
		Iterator<Row> it = venue.getRows().iterator();
		int seatsAvailableInVenue = 0;
		while (it.hasNext()) {
			seatsAvailableInVenue += it.next().numSeatsAvailable();
		}
		return seatsAvailableInVenue;

	}

	/**
	 * see TicketService for method summary
	 */
	@Override
	public SeatHold findAndHoldSeats(int numSeatsRequested, String customerEmail) {
		LOGGER.info("Attempting to find and hold " + numSeatsRequested + " seats in the venue");
		
		// case where there aren't enough remaining seats
		if (numSeatsRequested > numSeatsAvailable()) {
			LOGGER.info("Not enough remaining seats in the venue");
			return null;
		}

		SeatHold seatHold = new SeatHold(customerEmail, this);
		// go through each row and get the List of seats that are held
		List<Seat> heldSeats = new ArrayList<>(numSeatsRequested);
		List<Integer> seatRequests = new ArrayList<>();
		
		//case where the number of seats requested is larger than the size of a complete row
		//divide the request in groups of complete rows 
		if(numSeatsRequested > venue.getNumSeatsPerRow()){
			LOGGER.debug("Request is larger than the number of seats per row");
			seatRequests = divideSeatRequestsIntoCompleteRows(numSeatsRequested);
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
				List<Seat> temp = holdSeats(seatRequests.get(i), seatHold);
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
		seatHold.commitSeatHold(heldSeats);
		// add it to the list of venue seat holds
		this.seatHolds.put(seatHold.getSeatHoldId(), seatHold);
		// return the seat hold
		return seatHold;

	}

	/**
	 * loops through all the rows and sends back a seat hold list for the number
	 * of seats requested
	 * 
	 * @param seatsRequested
	 * @return held seats
	 */
	private List<Seat> holdSeats(int seatsRequested, SeatHold seatHold) {
		List<Seat> heldSeats = new ArrayList<>(seatsRequested);
		Iterator<Entry<Integer, Row>> it = rows.entrySet().iterator();

		while (it.hasNext() && heldSeats.isEmpty()) {
			Row currentRow = it.next().getValue();
			heldSeats = currentRow.holdSeats(seatsRequested, seatHold);
		}
		return heldSeats;
	}
	
	/**
	 * method used to divide requests into groups of complete rows and then remainder
	 * @param numSeatsRequested
	 * @return list of groupings of seats to be held
	 */
	public List<Integer> divideSeatRequestsIntoCompleteRows(int numSeatsRequested){
		int numberOfCompleteRows = numSeatsRequested / this.numSeatsPerRow;
		int remainder = numSeatsRequested % this.numSeatsPerRow;
		
		List<Integer> seatRequests = new ArrayList<>(numberOfCompleteRows + 1);
		for(int i = 0; i < numberOfCompleteRows; i++){
			seatRequests.add(this.numSeatsPerRow);
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

		// retrieve the seat hold by id
		SeatHold seatHold = seatHolds.get(seatHoldId);
		
		//determine if the seatHold is no longer valid
		if(seatHold.isNotValid()){
			return null;
		}
		
		// reserve the seats based on the seat hold
		for (Seat seat : seatHold.getSeatsHeld()) {
			seat.reserveSeat(customerEmail);
		}
		// return confirmation Id and populate it in a list for later retrieval
		// if necessary
		SeatReservation reservation = new SeatReservation(customerEmail, seatHold.getSeatsHeld());
		seatReservations.put(reservation.getConfirmationId(), reservation);
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
	public String printVenue() {
		//decide whether to print it to the console based on the size of the venue
		if(this.numRows > 40 || this.numSeatsPerRow > 40){
			return SEAT_MAP_PRINT_ERROR_MSG;
		}
		Iterator<Entry<Integer, Row>> it = rows.entrySet().iterator();
		String venueModel = "";
		while (it.hasNext()) {
			venueModel += it.next().getValue().print() + "\n";
		}
		venueModel = "VENUE SEAT MAP \n\nA = Available, H = Held, R = Reserved \n\n" + venueModel + "\n\n";
		return venueModel;
	}

}
