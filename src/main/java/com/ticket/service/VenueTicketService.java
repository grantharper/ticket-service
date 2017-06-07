package com.ticket.service;

import com.ticket.domain.SeatHold;

/**
 * extension of the Ticket Service which includes the ability to print a venue map
 */
public interface VenueTicketService extends TicketService{

	/**
	 * prints a map of the venue
	 * @return string representation of the venue's seat map
	 */
	String printVenue();
	
	/**
	 * invalidates a seat hold
	 */
	void invalidateHold(SeatHold seatHold);
	
}
