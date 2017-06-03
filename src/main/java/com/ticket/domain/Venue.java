package com.ticket.domain;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ticket.App;
import com.ticket.service.TicketService;

/**
 * The venue object will describe a venue with a given number of rows of seats
 * It will allow for seat reservations
 *
 */
public class Venue implements TicketService {

	public static final Logger LOGGER = LoggerFactory.getLogger(Venue.class);

	/**
	 * the hold duration for all venues in this application
	 */
	public static final Duration HOLD_DURATION = Duration.ofSeconds(5);

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
		seatRequests.add(numSeatsRequested);

		while (heldSeats.size() < numSeatsRequested) {

			for (Integer request : seatRequests) {
				List<Seat> temp = holdSeats(request);
				if (!temp.isEmpty()) {
					heldSeats.addAll(temp);
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
		SeatHold seatHold = new SeatHold(heldSeats, customerEmail);
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

	public int getVenueId() {
		return venueId;
	}

	public void printVenue() {
		Iterator<Entry<Integer, Row>> it = rows.entrySet().iterator();
		String venueModel = "\n";
		while (it.hasNext()) {
			venueModel += it.next().getValue().print() + "\n";
		}
		LOGGER.info(venueModel);
	}

}
