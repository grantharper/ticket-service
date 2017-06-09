package com.ticket.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The venue object will describe a venue with a given number of rows of seats
 * It will allow for seat reservations
 *
 */
/**
 *
 */
@Entity
public class Venue {

	public static final Logger LOGGER = LoggerFactory.getLogger(Venue.class);

	/**
	 * the unique identifier for the venue
	 */
	@Id
	private Integer venueId;

	/**
	 * the rows in the venue
	 */
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "venue")
	@OrderBy("rowId ASC")
	private Set<Row> rows = new LinkedHashSet<Row>();

	/**
	 * the seat holds for the venue
	 */
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "venue")
	@OrderBy("seatHoldId ASC")
	private Set<SeatHold> seatHolds = new LinkedHashSet<SeatHold>();

	/**
	 * the seat reservations for the venue
	 */
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "venue")
	@OrderBy("seatReservationId ASC")
	private Set<SeatReservation> seatReservations = new LinkedHashSet<SeatReservation>();


	public Venue(){}
	
	/**
	 * instantiates the venue
	 * 
	 * @param venueId
	 *            unique identifier for the venue
	 */
	public Venue(Integer venueId) {
		this.venueId = venueId;
	}
	
	/**
	 * determines the number of seats available in the venue
	 * @return number of seats available
	 */
	public int numSeatsAvailable() {
		Iterator<Row> it = this.getRows().iterator();
		int seatsAvailableInVenue = 0;
		while (it.hasNext()) {
			seatsAvailableInVenue += it.next().numSeatsAvailable();
		}
		return seatsAvailableInVenue;
	}
	
	/**
	 * locates and compiles list of the best available seats in the venue that can be held
	 * @param numSeatsRequested number of seats requested
	 * @param customerEmail email of the customer requesting the seats
	 * @return the SeatHold object containing which seats can be held
	 */
	public SeatHold findAndHoldSeats(int numSeatsRequested, String customerEmail){
		// case where there aren't enough remaining seats
				if (numSeatsRequested > numSeatsAvailable()) {
					LOGGER.info("Not enough remaining seats in the venue");
					return null;
				}

				SeatHold seatHold = new SeatHold(customerEmail, this);
				// go through each row and get the List of seats that are held
				List<Seat> heldSeats = new ArrayList<>(numSeatsRequested);
				List<Integer> seatRequests = new ArrayList<>();
				
				//case where the number of seats requested is larger than the size of a complete row
				//divide the request in groups of complete rows 
				if(numSeatsRequested > getNumberOfSeatsPerRow()){
					LOGGER.debug("Request is larger than the number of seats per row");
					seatRequests = divideSeatRequestsIntoCompleteRows(numSeatsRequested);
				}
				//case where the number of seats requested is smaller than the size of a complete row
				else{
					LOGGER.debug("Request is smaller than the number of seats per row");
					seatRequests.add(numSeatsRequested);
				}

				//attempt to find seats while the list of located seats is smaller than the number requested
				while (heldSeats.size() < numSeatsRequested) {

					//loop through each divided up request and hold available seats
					for (int i = 0; i < seatRequests.size(); i++ ) {
						List<Seat> temp = holdSeats(seatRequests.get(i), seatHold, this);
						if (!temp.isEmpty()) {
							//if seats were held, add them to the heldSeats list
							heldSeats.addAll(temp);
							//remove this request from the seatRequests
							seatRequests.remove(i);
							//exit if the total request has been fulfilled
							if (heldSeats.size() >= numSeatsRequested) {
								break;
							}
							//account for the removal of the request from the list by decrementing the counter
							i--;
						}
					}
					
					//if the seat requests could not all be fulfilled, divide the remaining requests by two and attempt to fulfill the smaller grouped requests
					List<Integer> updatedSeatRequests = new ArrayList<>();
					for (Integer request : seatRequests) {
						int halvedRequest = request / 2;
						if(halvedRequest < 1){
							//unlikely scenario for a multithreaded app where a seat request of 1 cannot be fulfilled so we attempt to divide it
							seatHold.invalidate();
							return null;
						}
						if ((request % 2) == 1) {
							updatedSeatRequests.add(halvedRequest + 1);
						} else {
							updatedSeatRequests.add(halvedRequest);
						}
						// add the potentially smaller request to the list last so
						// we find bigger groups together first
						updatedSeatRequests.add(halvedRequest);

					}
					seatRequests = updatedSeatRequests;

				}
				
				seatHold.setSeatsHeld(heldSeats);
				
				return seatHold;
	}
	
	
	/**
	 * method used to divide requests into groups of complete rows and then remainder
	 * @param numSeatsRequested
	 * @return list of groupings of seats to be held
	 */
	List<Integer> divideSeatRequestsIntoCompleteRows(int numSeatsRequested){
		int numberOfCompleteRows = numSeatsRequested / getNumberOfSeatsPerRow();
		int remainder = numSeatsRequested % getNumberOfSeatsPerRow();
		
		List<Integer> seatRequests = new ArrayList<>(numberOfCompleteRows + 1);
		for(int i = 0; i < numberOfCompleteRows; i++){
			seatRequests.add(getNumberOfSeatsPerRow());
		}
		if(remainder > 0){
			seatRequests.add(remainder);
		}
		return seatRequests;
	}
	
	/**
	 * loops through all the rows and sends back a seat hold list for the number
	 * of seats requested
	 * 
	 * @param seatsRequested
	 * @return held seats
	 */
	public List<Seat> holdSeats(int seatsRequested, SeatHold seatHold, Venue venue) {
		List<Seat> heldSeats = new ArrayList<>(seatsRequested);
		Iterator<Row> it = venue.getRows().iterator();

		while (it.hasNext() && heldSeats.isEmpty()) {
			Row currentRow = it.next();
			heldSeats = currentRow.holdSeats(seatsRequested, seatHold);
		}
		return heldSeats;
	}
	
	
	/**
	 * method to determine the number of seats per row in a venue. This assumes a rectangular shaped venue with even rows
	 * @param venue
	 * @return number of seats per row in the venue
	 */
	public int getNumberOfSeatsPerRow(){
		return this.getRows().iterator().next().getSeats().size();
	}
	
	/**
	 * standard error message when the venue seat map is too large to display
	 */
	public static final String SEAT_MAP_PRINT_ERROR_MSG = "Seat map is too large to display. Only maps of 40x40 or smaller can be displayed.\n\n";
	
	/**
	 * @param venue
	 * @return
	 */
	public String printVenue() {
		//decide whether to print it to the console based on the size of the venue
		if(this.getRows().size() > 40 || getNumberOfSeatsPerRow() > 40){
			return SEAT_MAP_PRINT_ERROR_MSG;
		}
		Iterator<Row> it = this.getRows().iterator();
		String venueModel = "";
		while (it.hasNext()) {
			venueModel += it.next().print() + "\n";
		}
		venueModel = "VENUE SEAT MAP \n\n" + 
				Seat.SEAT_AVAILABLE_CODE +  " = Available, " + 
				Seat.SEAT_HELD_CODE + " = Held, " + 
				Seat.SEAT_RESERVED_CODE + " = Reserved \n\n" + 
				venueModel + "\n\n";
		return venueModel;
	}

	/**
	 * @return the venueId
	 */
	public Integer getVenueId() {
		return venueId;
	}

	/**
	 * @param venueId the venueId to set
	 */
	public void setVenueId(Integer venueId) {
		this.venueId = venueId;
	}

	/**
	 * @return the rows
	 */
	public Set<Row> getRows() {
		return rows;
	}

	/**
	 * @param rows the rows to set
	 */
	public void setRows(Set<Row> rows) {
		this.rows = rows;
	}

	/**
	 * @return the seatHolds
	 */
	public Set<SeatHold> getSeatHolds() {
		return seatHolds;
	}

	/**
	 * @param seatHolds the seatHolds to set
	 */
	public void setSeatHolds(Set<SeatHold> seatHolds) {
		this.seatHolds = seatHolds;
	}
	
	/**
	 * @return the seatReservations
	 */
	public Set<SeatReservation> getSeatReservations() {
		return seatReservations;
	}

	/**
	 * @param seatReservations the seatReservations to set
	 */
	public void setSeatReservations(Set<SeatReservation> seatReservations) {
		this.seatReservations = seatReservations;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Venue [venueId=" + venueId + ", rows=" + rows + ", seatHolds=" + seatHolds + "]";
	}	
	
	
}
