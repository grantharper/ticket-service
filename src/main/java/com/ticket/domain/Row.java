package com.ticket.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Row {

	public static final Logger LOGGER = LoggerFactory.getLogger(Row.class);

	/**
	 * unique identifier for the row
	 */
	private final int rowId;

	/**
	 * list of all the seats in the row
	 */
	private Map<Integer, Seat> seats = new HashMap<>();

	/**
	 * venue where the row is located
	 */
	private Venue venue;

	/**
	 * number of seats in the row
	 */
	private final int numSeats;

	/**
	 * instantiates a row in a given venue
	 * 
	 * @param venueId
	 *            the unique identifier for the venue
	 * @param rowId
	 *            the unique identifier for the row
	 */
	public Row(final int rowId, final int numSeats, Venue venue) {
		this.rowId = rowId;
		this.venue = venue;
		this.numSeats = numSeats;
		for (int i = 1; i <= numSeats; i++) {
			this.seats.put(i, new Seat(i, venue, this));
		}
	}

	/**
	 * loops through the seats in the row to determine how many are available
	 * 
	 * @return number of available seats
	 */
	public int numSeatsAvailable() {

		Iterator<Entry<Integer, Seat>> it = seats.entrySet().iterator();
		int seatsAvailableInRow = 0;
		while (it.hasNext()) {
			if (it.next().getValue().isAvailable()) {
				seatsAvailableInRow++;
			}
		}
		return seatsAvailableInRow;
	}

	/**
	 * Places a hold on the number of seats requested if they are available
	 * 
	 * @param numSeatsRequested
	 *            the number of seats to be held
	 * @return list of seats that have been held. If unsuccessful, returns null
	 */
	List<Seat> holdSeats(int numSeatsRequested, SeatHold seatHold) {
		// initialize with the number requested to avoid having to recreate it
		// internally
		List<Seat> heldSeats = new ArrayList<>(numSeatsRequested);

		// if numberOfSeats is greater than the seats in the row
		if (numSeatsRequested > seats.size()) {
			return heldSeats;
		}

		// case where we can start from the middle
		if (seats.get(1).isAvailable()) {
			for (int i = 1; i <= numSeatsRequested; i++) {
				seats.get(i).placeHold(seatHold);
				heldSeats.add(seats.get(i));
			}
			return heldSeats;
		}

		// case where we can't start from the middle. Start with whichever side
		// has the first available seat since this side will have the most seats
		// available
		for (int i = 2; i <= seats.size(); i++) {

			if (seats.get(i).isAvailable()) {
				heldSeats = holdRightOrLeftSeats(numSeatsRequested, i, seatHold);
				return heldSeats;
			} else {
				// some optimization to short circuit the holding of seats if I
				// can already tell there won't be enough in this row
				if (numSeatsRequested > ((numSeats - i) / 2) + (numSeats - i) % 2) {
					return heldSeats;
				}
			}
		}

		// could not find available seats in the row
		return heldSeats;

	}

	/**
	 * 
	 * @param numSeats
	 *            number of seats to be held
	 * @param startingSeat
	 *            the first available seat on that side
	 * @return
	 */
	private List<Seat> holdRightOrLeftSeats(int numSeats, int startingSeat, SeatHold seatHold) {
		List<Seat> heldSeats = new ArrayList<>(numSeats);
		List<Seat> availableSeats = new ArrayList<>(numSeats);

		// add the seats on the left or right side of center by moving by twos
		for (int i = startingSeat; i <= seats.size(); i += 2) {
			if (seats.get(i).isAvailable()) {
				availableSeats.add(seats.get(i));
			}
			if (availableSeats.size() == numSeats) {
				break;
			}
		}

		// if there are enough seats available, place hold on the seats
		if (availableSeats.size() >= numSeats) {
			for (Seat seat : availableSeats) {
				seat.placeHold(seatHold);
				heldSeats.add(seat);
			}
		} 
		return heldSeats;
	
	}

	/**
	 * prints a map of the seats in the row
	 * 
	 * @return string representation of the row
	 */
	public String print() {
		Iterator<Entry<Integer, Seat>> it = seats.entrySet().iterator();
		String rowString = " ";
		while (it.hasNext()) {
			Seat currentSeat = it.next().getValue();
			// takes into account the odds being on the left and the evens being
			// on the right
			if ((currentSeat.getSeatId()) % 2 == 1) {
				rowString = " " + currentSeat.print() + rowString;
			} else {
				rowString = rowString + currentSeat.print() + " ";
			}
		}
		return rowString;
	}

	/**
	 * @return the rowId
	 */
	public int getRowId() {
		return rowId;
	}

	/**
	 * @return the numSeats
	 */
	public int getNumSeats() {
		return numSeats;
	}

}
