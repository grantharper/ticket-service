package com.ticket.domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * describes the information associated with a seat hold placed by a customer
 */
public class SeatHold {

	public static final Logger LOGGER = LoggerFactory.getLogger(SeatHold.class);
	
	/**
	 * the next id for the seat hold
	 */
	private static int nextSeatHoldId = 1111;
	
	/**
	 * the unique id for the seat hold
	 */
	private final int seatHoldId;
	
	/**
	 * the list of seats that are held by the seat hold
	 */
	private List<Seat> seatsHeld;
	
	/**
	 * the email of the customer who owns the seat hold
	 */
	private final String customerEmail;
	
	/**
	 * the time when the seat hold will expire
	 */
	private LocalDateTime holdExpiration;
	
	/**
	 * flag to indicate that the hold is in progress
	 */
	private boolean inProgress;
	
	/**
	 * the venue where the seat hold is located
	 */
	private final Venue venue;
	
	/**
	 * instantiation of the seat hold
	 * @param seatsHeld the seats held
	 * @param customerEmail the email of the customer
	 * @param holdExpiration the time when the seat hold will expire
	 */
	public SeatHold(String customerEmail, Venue venue){
		this.seatHoldId = nextSeatHoldId;
		nextSeatHoldId++;
		this.venue = venue;
		this.customerEmail = customerEmail;
		this.inProgress = true;
	}
	
	/**
	 * commits the seat hold and establishes the time when it will expire
	 * @param seatsHeld the seats held
	 * @param holdExpiration the time when the seat hold will expire
	 */
	public void commitSeatHold(List<Seat> seatsHeld){
		this.seatsHeld = seatsHeld;
		this.holdExpiration = LocalDateTime.now().plus(this.venue.holdDuration);
		this.inProgress = false;
	}
	
	/**
	 * @return the inProgress
	 */
	public boolean isInProgress() {
		return inProgress;
	}
	
	public void invalidate(){
		this.inProgress = false;
		if(this.holdExpiration != null){
			this.holdExpiration = null;
		}
	}

	/**
	 * @return whether the seat hold is currently holding the seats
	 */
	public boolean isHolding(){
		if(this.inProgress){
			return true;
		}
		else 
		if(this.holdExpiration != null && LocalDateTime.now().isBefore(holdExpiration)){
			return true;
		}
		return false;
	}
	
	/**
	 * method to represent whether a seat hold is in effect in plain English
	 * @return whether seat hold is in effect
	 */
	public boolean isNotValid(){
		return !isHolding();
	}
	
	/**
	 * @return the number of seconds until seat hold expiration truncated to the nearest second
	 */
	public String printSecondsToExpiration(){
		Duration timeToExpire = Duration.between(LocalDateTime.now(), holdExpiration);
		if(timeToExpire.toMillis() > 0){
			return "" + (timeToExpire.toMillis() / 1000) + " seconds";
		}
		return "0 seconds";
		
	}
	
	
	/**
	 * @return the seatHoldId
	 */
	public int getSeatHoldId() {
		return seatHoldId;
	}


	/**
	 * @return the seatsHeld
	 */
	public List<Seat> getSeatsHeld() {
		return seatsHeld;
	}


	/**
	 * @return the customerEmail
	 */
	public String getCustomerEmail() {
		return customerEmail;
	}


	/**
	 * @return the holdExpiration
	 */
	public LocalDateTime getHoldExpiration() {
		return holdExpiration;
	}


}
