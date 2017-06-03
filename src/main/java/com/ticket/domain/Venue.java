package com.ticket.domain;

import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.ticket.service.TicketService;

/**
 * The venue object will describe a venue with a given number of rows of seats
 * It will allow for seat reservations
 *
 */
public class Venue implements TicketService{
	
	/**
	 * the hold duration for all venues in this application
	 */
	public static final Duration HOLD_DURATION = Duration.ofSeconds(5);
	
	/**
	 * the unique identifier for the venue
	 */
	private final int venueId;
	
	/**
	 * the rows in the venue
	 */
	private Map<Integer, Row> rows = new HashMap<>();
	
	/**
	 * instantiates the venue
	 * @param venueId unique identifier for the venue
	 * @param numRows number of rows in the venue
	 * @param numSeatsPerRow number of seats per row in the venue
	 */
	public Venue(int venueId, int numRows, int numSeatsPerRow){
		this.venueId = venueId;
		for(int i = 1; i <= numRows; i++){
			this.rows.put(i, new Row(venueId, i, numSeatsPerRow));
		}
	}
	
	@Override
	public int numSeatsAvailable() {
		Iterator<Entry<Integer, Row>> it = rows.entrySet().iterator();
		int seatsAvailableInVenue = 0;
		while(it.hasNext()){
			seatsAvailableInVenue += it.next().getValue().numSeatsAvailable();
		}
		return seatsAvailableInVenue;

	}

	@Override
	public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String reserveSeats(int seatHoldId, String customerEmail) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getVenueId() {
		return venueId;
	}
	
	

}
