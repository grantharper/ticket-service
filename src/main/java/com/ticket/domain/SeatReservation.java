package com.ticket.domain;

import java.util.List;

public class SeatReservation {

	private static String nextConfirmationId = "VX3529";
	private final String customerEmail;
	private final String confirmationId;
	private final List<Seat> reservedSeats;

	public SeatReservation(String customerEmail, List<Seat> reservedSeats) {
		this.confirmationId = nextConfirmationId;
		updateConfirmationId();
		this.customerEmail = customerEmail;
		this.reservedSeats = reservedSeats;
	}

	private void updateConfirmationId() {
		nextConfirmationId = "VX" + (Integer.parseInt(nextConfirmationId.substring(2)) + 1);
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public String getConfirmationId() {
		return confirmationId;
	}

	public List<Seat> getReservedSeats() {
		return reservedSeats;
	}

}
