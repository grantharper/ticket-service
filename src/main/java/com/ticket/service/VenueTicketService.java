package com.ticket.service;

public interface VenueTicketService extends TicketService{

	/**
	 * prints a map of the venue
	 * @return string representation of the venue's seat map
	 */
	String printVenue();
	
}
