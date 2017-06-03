package com.ticket.domain;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Describes a seat in a venue and whether or not it is available to be held or
 * reserved
 *
 */
public class Seat {

	/**
	 * the venue where the seat is located
	 */
	private final int venueId;

	/**
	 * the row where the seat is located which cannot change (e.g. A, B)
	 */
	private final int rowId;

	/**
	 * the unique identifier for the seat
	 */
	private final int seatId;

	/**
	 * the time at which the hold was placed on the seat
	 */
	private LocalDateTime holdTime;

	/**
	 * true if a final reservation has been placed false if the seat has not
	 * been reserved
	 */
	private boolean reserved;

	/**
	 * email of the customer who reserved the seats
	 */
	private String customerReservationEmail;

	/**
	 * Instantiation of the seat
	 * 
	 * @param seatRow
	 *            the row where the seat is located
	 * @param seatNum
	 *            the number of the seat
	 */
	public Seat(int venueId, int rowId, int seatId) {
		this.venueId = venueId;
		this.rowId = rowId;
		this.seatId = seatId;
	}

	/**
	 * determines whether a seat is available for being held or reserved
	 * 
	 * @return an indicator as to whether the seat is available
	 */
	public boolean isAvailable() {
		if (reserved) {
			return false;
		} else if (isHeld()) {
			return false;
		}
		return true;
	}

	/**
	 * inspects the hold time on the seat, if present, to determine whether the
	 * hold has expired and the seat is available again
	 * 
	 * @return an indicator as to whether the seat is held
	 */
	public boolean isHeld() {
		if (holdTime == null) {
			return false;
		} else if (holdTime.plus(Venue.HOLD_DURATION).isBefore(LocalDateTime.now())) {
			return false;
		} else {
			return true;
		}

	}

	public LocalDateTime placeHold() {
		this.holdTime = LocalDateTime.now();
		return holdTime;
	}

	public void reserveSeat(String customerEmail) {
		this.reserved = true;
		this.customerReservationEmail = customerEmail;
	}

	public boolean isReserved() {
		return reserved;
	}

	public LocalDateTime getHoldTime() {
		return holdTime;
	}

	public int getVenueId() {
		return venueId;
	}

	public int getRowId() {
		return rowId;
	}

	public int getSeatId() {
		return seatId;
	}

	public String print() {
		if (this.isReserved()) {
			return "R ";
		} else if (this.isHeld()) {
			return "H ";
		} else {
			return "A ";
		}

	}

}
