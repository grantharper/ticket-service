package com.ticket.domain;

import java.time.LocalDateTime;
import java.util.List;

public class SeatHold {

	private static int nextSeatHoldId = 1111;
	private final int seatHoldId;
	private final List<Seat> seatsHeld;
	private final String customerEmail;
	private final LocalDateTime holdExpiration;

	public SeatHold(List<Seat> seatsHeld, String customerEmail, LocalDateTime holdExpiration) {
		this.seatHoldId = nextSeatHoldId;
		nextSeatHoldId++;
		this.seatsHeld = seatsHeld;
		this.customerEmail = customerEmail;
		this.holdExpiration = holdExpiration;
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

	public LocalDateTime getHoldExpiration() {
		return holdExpiration;
	}
	
	public boolean isExpired(){
		if(LocalDateTime.now().isAfter(holdExpiration)){
			return true;
		}
		return false;
	}

}
