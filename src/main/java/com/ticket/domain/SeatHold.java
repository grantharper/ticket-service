package com.ticket.domain;

import java.util.List;

public class SeatHold {

	private static int nextSeatHoldId = 1111;
	private final int seatHoldId;
	private final List<Seat> seatsHeld;
	private final String customerEmail;

	public SeatHold(List<Seat> seatsHeld, String customerEmail) {
		this.seatHoldId = nextSeatHoldId;
		nextSeatHoldId++;
		this.seatsHeld = seatsHeld;
		this.customerEmail = customerEmail;
	}

	public int getSeatHoldId() {
		return seatHoldId;
	}

	public List<Seat> getSeatsHeld() {
		return seatsHeld;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

}
