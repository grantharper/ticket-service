package com.ticket.domain;

import java.time.Duration;
import java.time.LocalDateTime;
/**
 * Describes a seat in a venue and whether or not it is available to be held or reserved
 *
 */
public class Seat {

	/**
	 * the venue where the seat is located
	 */
	private final Venue venue;
	
	/**
	 * the row where the seat is located which cannot change (e.g. A, B)
	 */
	private final String seatRow;
	
	/**
	 * the seat number of the seat which cannot change (e.g. 1, 2)
	 */
	private final int seatNum;
	
	/**
	 * the time at which the hold was placed on the seat
	 */
	private LocalDateTime holdTime;
	
	/**
	 * true if a final reservation has been placed
	 * false if the seat has not been reserved
	 */
	private boolean reserved;
	
	/**
	 * Instantiation of the seat
	 * @param seatRow the row where the seat is located 
	 * @param seatNum the number of the seat
	 */
	public Seat(Venue venue, String seatRow, int seatNum){
		this.venue = venue;
		this.seatRow = seatRow;
		this.seatNum = seatNum;
	}
	
	/**
	 * inspects the hold time on the seat, if present, to determine whether the hold has expired and the seat is available again
	 * @return an indicator as to whether the seat is held
	 */
	public boolean isHeld(){
		if(holdTime == null){
			return false;
		}else if (holdTime.plus(venue.getHoldDuration()).isBefore(LocalDateTime.now())){
			return false;
		}else{
			return true;
		}
		
	}
	
	public LocalDateTime placeHold(){
		this.holdTime = LocalDateTime.now();
		return holdTime;
	}
	

	public boolean isReserved() {
		return reserved;
	}

	public void setReserved(boolean reserved) {
		this.reserved = reserved;
	}

	public LocalDateTime getHoldTime() {
		return holdTime;
	}

	public String getSeatRow() {
		return seatRow;
	}

	public int getSeatNum() {
		return seatNum;
	}
	
	
	
	
}
