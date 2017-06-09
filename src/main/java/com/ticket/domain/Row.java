package com.ticket.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
public class Row {

	public static final Logger LOGGER = LoggerFactory.getLogger(Row.class);

	/**
	 * unique identifier for the row throughout the system
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer rowId;
	
	/**
	 * the user-friendly row number in a given venue
	 */
	private Integer rowNumber;

	/**
	 * list of all the seats in the row
	 */
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "row")
	@OrderBy("seatNumber ASC")
	private Set<Seat> seats = new LinkedHashSet<Seat>();

	/**
	 * venue where the row is located
	 */
	@ManyToOne
	private Venue venue;

	public Row() {

	}

	/**
	 * instantiates a row in a given venue
	 * 
	 * @param venueId
	 *            the unique identifier for the venue
	 * @param rowId
	 *            the unique identifier for the row
	 */
	public Row(Integer rowNumber, Venue venue) {
		this.rowNumber = rowNumber;
		this.venue = venue;
	}

	/**
	 * loops through the seats in the row to determine how many are available
	 * 
	 * @return number of available seats
	 */
	public int numSeatsAvailable() {

		Iterator<Seat> it = seats.iterator();
		int seatsAvailableInRow = 0;
		while (it.hasNext()) {
			if (it.next().isAvailable()) {
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
	public List<Seat> holdSeats(int numSeatsRequested, SeatHold seatHold) {
		// initialize with the number requested to avoid having to recreate it
		// internally
		List<Seat> heldSeats = new ArrayList<>(numSeatsRequested);

		// if numberOfSeats is greater than the seats in the row
		if (numSeatsRequested > seats.size()) {
			return heldSeats;
		}
		
		//put the seats into an array list for easier access
		List<Seat> seats = new ArrayList<>(this.seats);

		// case where we can start from the middle
		if (seats.get(0).isAvailable()) {
			for (int i = 0; i < numSeatsRequested; i++) {
				seats.get(i).placeHold(seatHold);
				heldSeats.add(seats.get(i));
			}
			return heldSeats;
		}

		// case where we can't start from the middle. Start with whichever side
		// has the first available seat since this side will have the most seats
		// available
		for (int i = 1; i < seats.size(); i++) {

			if (seats.get(i).isAvailable()) {
				heldSeats = holdRightOrLeftSeats(numSeatsRequested, i, seatHold, seats);
				return heldSeats;
			} else {
				// some optimization to short circuit the holding of seats if I
				// can already tell there won't be enough in this row
				if (numSeatsRequested > ((this.seats.size() - i) / 2) + (this.seats.size() - i) % 2) {
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
	private List<Seat> holdRightOrLeftSeats(int numSeats, int startingSeat, SeatHold seatHold, List<Seat> seats) {
		List<Seat> heldSeats = new ArrayList<>(numSeats);
		List<Seat> availableSeats = new ArrayList<>(numSeats);

		// add the seats on the left or right side of center by moving by twos
		for (int i = startingSeat; i < seats.size(); i += 2) {
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
		Iterator<Seat> it = seats.iterator();
		String rowString = " ";
		while (it.hasNext()) {
			Seat currentSeat = it.next();
			// takes into account the odds being on the left and the evens being
			// on the right
			if ((currentSeat.getSeatNumber()) % 2 == 1) {
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
	public Integer getRowId() {
		return rowId;
	}

	/**
	 * @param rowId the rowId to set
	 */
	public void setRowId(Integer rowId) {
		this.rowId = rowId;
	}

	/**
	 * @return the rowNumber
	 */
	public Integer getRowNumber() {
		return rowNumber;
	}

	/**
	 * @param rowNumber the rowNumber to set
	 */
	public void setRowNumber(Integer rowNumber) {
		this.rowNumber = rowNumber;
	}

	/**
	 * @return the seats
	 */
	public Set<Seat> getSeats() {
		return seats;
	}

	/**
	 * @param seats the seats to set
	 */
	public void setSeats(Set<Seat> seats) {
		this.seats = seats;
	}

	/**
	 * @return the venue
	 */
	public Venue getVenue() {
		return venue;
	}

	/**
	 * @param venue the venue to set
	 */
	public void setVenue(Venue venue) {
		this.venue = venue;
	}

	
	

}
