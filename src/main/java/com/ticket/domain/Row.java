package com.ticket.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Row {

	/**
	 * unique identifier for the row
	 */
	private final int rowId;

	/**
	 * unique identifier for the venue where the row is located
	 */
	private final int venueId;

	/**
	 * list of all the seats in the row
	 */
	private Map<Integer, Seat> seats = new HashMap<>();

	/**
	 * instantiates a row in a given venue
	 * 
	 * @param venueId
	 *            the unique identifier for the venue
	 * @param rowId
	 *            the unique identifier for the row
	 */
	public Row(final int venueId, final int rowId, final int numSeats) {
		this.venueId = venueId;
		this.rowId = rowId;
		for (int i = 0; i < numSeats; i++) {
			this.seats.put(i + 1, new Seat(venueId, rowId, i + 1));
		}
	}
	
	public int numSeatsAvailable(){
		//TODO: consider optimization
		Iterator<Entry<Integer, Seat>> it = seats.entrySet().iterator();
		int seatsAvailableInRow = 0;
		while(it.hasNext()){
			if(it.next().getValue().isAvailable()){
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
	List<Seat> holdSeats(int numSeatsRequested) {
		//if numberOfSeats is greater than the seats in the row
		if(numSeatsRequested > seats.size()){
			return null;
		}
		
		// initialize with the number requested to avoid having to recreate it
		// internally
		List<Seat> heldSeats = new ArrayList<>(numSeatsRequested);

		// case where we can start from the middle
		if (seats.get(1).isAvailable()) {
			for (int i = 1; i <= numSeatsRequested; i++) {
				seats.get(i).placeHold();
				heldSeats.add(seats.get(i));
			}
			return heldSeats;
		}

		// case where we can't start from the middle. Start with whichever side has the first available seat
		for(int i = 2; i <= seats.size(); i++){
			
			if(seats.get(i).isAvailable()){
				heldSeats = holdRightOrLeftSeats(numSeatsRequested, i);
				return heldSeats;
			}else{
				// some optimization to short circuit the holding of seats if I can already tell there won't be enough
				if(numSeatsRequested > ((seats.size() - i) / 2) + (seats.size() - i) % 2){
					return null;
				}
			}
		}
		
		//could not find available seats in the row
		return null;

	}

	/**
	 * 
	 * @param numSeats number of seats to be held
	 * @param startingSeat the first available seat on that side
	 * @return
	 */
	private List<Seat> holdRightOrLeftSeats(int numSeats, int startingSeat) {
		List<Seat> heldSeats = new ArrayList<>(numSeats);
		List<Seat> availableSeats = new ArrayList<>(numSeats);
		//TODO: think about whether this check is necessary given the optimization of making sure there are enough seats remaining
		for (int i = startingSeat; i <= seats.size(); i+=2 ){
			if (seats.get(i).isAvailable()){
				availableSeats.add(seats.get(i));
			}
		}
		
		//if there are enough, hold the seats
		if(availableSeats.size() >= numSeats){
			for(Seat oddSeat: availableSeats){
				oddSeat.placeHold();
				heldSeats.add(oddSeat);
			}
			return heldSeats;
		}else{
			return null;
		}
	}
	
	

}
