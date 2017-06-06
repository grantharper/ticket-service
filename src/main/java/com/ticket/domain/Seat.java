package com.ticket.domain;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Describes a seat in a venue and its state regarding whether it is available, on hold, or reserved
 *
 */
public class Seat {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(Seat.class);
	
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
	 * venue where the seat is located
	 */
	private final Venue venue;
	
	/**
	 * row where the seat is located
	 */
	private final Row row;
	
	/**
	 * Instantiation of the seat
	 * 
	 * @param seatRow
	 *            the row where the seat is located
	 * @param seatNum
	 *            the number of the seat
	 */
	public Seat(int seatId, final Venue venue, final Row row) {
		this.seatId = seatId;
		this.venue = venue;
		this.row = row;
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
		} else if (holdTime.plus(venue.getHoldDuration()).isBefore(LocalDateTime.now())) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * code for the seat being available
	 */
	public static final String SEAT_AVAILABLE_CODE = "A";
	
	/**
	 * code for the seat as held
	 */
	public static final String SEAT_HELD_CODE = "H";

	/**
	 * code for the seat being available
	 */
	public static final String SEAT_RESERVED_CODE = "R";
	
	/**
	 * 
	 * @return string representation of the state of the seat
	 */
	public String print() {
		if (this.isReserved()) {
			return SEAT_RESERVED_CODE;
		} else if (this.isHeld()) {
			return SEAT_HELD_CODE;
		} else {
			return SEAT_AVAILABLE_CODE;
		}
	}
	
	/**
	 * places a hold on the seat by establishing the time when the seat was held
	 * @return time when the seat was held
	 */
	//TODO: change this method to use a passed in time (maybe SeatHold object) so that there is not disparity between the hold times
	public LocalDateTime placeHold() {
		this.holdTime = LocalDateTime.now();
		return holdTime;
	}

	/**
	 * reserves the seat for the customer email passed in
	 * @param customerEmail email of the customer
	 */
	public void reserveSeat(String customerEmail) {
		this.reserved = true;
		this.customerReservationEmail = customerEmail;
	}

	/**
	 * @return the seatId
	 */
	public int getSeatId() {
		return seatId;
	}

	/**
	 * @return the holdTime
	 */
	public LocalDateTime getHoldTime() {
		return holdTime;
	}

	/**
	 * @return the reserved
	 */
	public boolean isReserved() {
		return reserved;
	}

	/**
	 * @return the customerReservationEmail
	 */
	public String getCustomerReservationEmail() {
		return customerReservationEmail;
	}

}
