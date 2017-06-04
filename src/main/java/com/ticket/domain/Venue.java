package com.ticket.domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ticket.service.VenueTicketService;
import com.util.AppProperties;

/**
 * The venue object will describe a venue with a given number of rows of seats
 * It will allow for seat reservations
 *
 */
public class Venue implements VenueTicketService {

	public static final Logger LOGGER = LoggerFactory.getLogger(Venue.class);

	/**
	 * the hold duration for all venues in this application
	 */
	public static final Duration HOLD_DURATION = Duration.ofSeconds(10);

	/**
	 * the unique identifier for the venue
	 */
	private final int venueId;

	/**
	 * the rows in the venue
	 */
	private Map<Integer, Row> rows = new HashMap<>();

	/**
	 * the seat holds for the venue
	 */
	private Map<Integer, SeatHold> seatHolds = new HashMap<>();

	/**
	 * the seat reservations for the venue
	 */
	private Map<String, SeatReservation> seatReservations = new HashMap<>();
	
	/**
	 * number of rows in the venue
	 */
	private int numRows;
	
	/**
	 * number of seats per row in the venue
	 */
	private int numSeatsPerRow;


	/**
	 * instantiates the venue
	 * 
	 * @param venueId
	 *            unique identifier for the venue
	 * @param numRows
	 *            number of rows in the venue
	 * @param numSeatsPerRow
	 *            number of seats per row in the venue
	 */
	public Venue(int venueId, int numRows, int numSeatsPerRow) {
		this.venueId = venueId;
		this.numRows = numRows;
		this.numSeatsPerRow = numSeatsPerRow;
		
		for (int i = 1; i <= numRows; i++) {
			this.rows.put(i, new Row(venueId, i, numSeatsPerRow));
		}
	}
	

	@Override
	public int numSeatsAvailable() {
		Iterator<Entry<Integer, Row>> it = rows.entrySet().iterator();
		int seatsAvailableInVenue = 0;
		while (it.hasNext()) {
			seatsAvailableInVenue += it.next().getValue().numSeatsAvailable();
		}
		return seatsAvailableInVenue;

	}

	@Override
	public SeatHold findAndHoldSeats(int numSeatsRequested, String customerEmail) {

		// case where there aren't enough remaining seats
		if (numSeatsRequested > numSeatsAvailable()) {
			return null;
		}

		// go through each row and get the List of seats that are held
		List<Seat> heldSeats = new ArrayList<>(numSeatsRequested);
		List<Integer> seatRequests = new ArrayList<>();
		
		//case where the number of seats requested is larger than the size of a complete row
		//divide the request in groups of complete rows 
		if(numSeatsRequested > numSeatsPerRow){
			seatRequests = divideSeatRequestsIntoCompleteRows(numSeatsRequested);
		}
		//case where the number of seats requested is smaller than the size of a complete row
		else{
			seatRequests.add(numSeatsRequested);
		}

		
		while (heldSeats.size() < numSeatsRequested) {

			for (int i = 0; i < seatRequests.size(); i++ ) {
				List<Seat> temp = holdSeats(seatRequests.get(i));
				if (!temp.isEmpty()) {
					heldSeats.addAll(temp);
					seatRequests.remove(i);
					if (heldSeats.size() >= numSeatsRequested) {
						break;
					} 
					i--;
				}

				// else{
				// //if the seats return null, the group will need to be halved
				// since there are no more remaining rows with this many seats
				// break;
				// }
			}

			if (heldSeats.size() >= numSeatsRequested) {
				break;
			} else {
				// divide up the requests into twice the number of previous
				// groups with half the size
				List<Integer> updatedSeatRequests = new ArrayList<>();
				for (Integer request : seatRequests) {
					int halvedRequest = request / 2;
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
		}

		// populate the SeatHold with the list of seats and customer info and
		SeatHold seatHold = new SeatHold(heldSeats, customerEmail, LocalDateTime.now().plus(HOLD_DURATION));
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
	private List<Seat> holdSeats(int seatsRequested) {
		List<Seat> heldSeats = new ArrayList<>(seatsRequested);
		Iterator<Entry<Integer, Row>> it = rows.entrySet().iterator();

		while (it.hasNext() && heldSeats.isEmpty()) {
			Row currentRow = it.next().getValue();
			heldSeats = currentRow.holdSeats(seatsRequested);
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

	// attempt at optimization
	private List<Seat> holdSeatsOptimized(int totalSeatsRequested) {
		List<Seat> heldSeats = new ArrayList<>(totalSeatsRequested);
		Iterator<Entry<Integer, Row>> it = rows.entrySet().iterator();

		int groupSeatsRequested = totalSeatsRequested;
		int numGroups = 1;

		while (heldSeats.size() < totalSeatsRequested) {
			while (it.hasNext() && heldSeats.size() < totalSeatsRequested) {
				List<Seat> temp = null;
				Row currentRow = it.next().getValue();
				for (int i = 0; i < numGroups; i++) {
					temp = currentRow.holdSeats(groupSeatsRequested);
					if (temp == null) {
						break;
					} else {
						heldSeats.addAll(temp);
					}
				}

			}

		}

		return heldSeats;
	}

	@Override
	public String reserveSeats(int seatHoldId, String customerEmail) {

		// retrieve the seat hold by id
		SeatHold seatHold = seatHolds.get(seatHoldId);
		
		//determine if the seatHold has expired
		if(seatHold.isExpired()){
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
	 * prints a visual representation of the venue's rows and seats as well as their state
	 * A = available, H = held, R = reserved
	 */
	public String printVenue() {
		Iterator<Entry<Integer, Row>> it = rows.entrySet().iterator();
		String venueModel = "";
		while (it.hasNext()) {
			venueModel += it.next().getValue().print() + "\n";
		}
		LOGGER.info(venueModel);
		venueModel = "VENUE SEAT MAP \n\nA = Available, H = Held, R = Reserved \n\n" + venueModel + "\n\n";
		return venueModel;
	}

	public int getVenueId() {
		return venueId;
	}

	public Map<Integer, Row> getRows() {
		return rows;
	}

	public Map<Integer, SeatHold> getSeatHolds() {
		return seatHolds;
	}

	public Map<String, SeatReservation> getSeatReservations() {
		return seatReservations;
	}

	
	
	
}
