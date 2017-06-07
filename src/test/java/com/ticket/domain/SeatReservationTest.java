package com.ticket.domain;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class SeatReservationTest {

	@Test
	public void testNextAvailableId() {
		String id = SeatReservation.getNextConfirmationId();
		SeatReservation seatReservation = new SeatReservation("", new ArrayList<>());
		assertEquals(id, seatReservation.getConfirmationId());
	}

}
