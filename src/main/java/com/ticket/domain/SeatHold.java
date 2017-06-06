package com.ticket.domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
	private final List<Seat> seatsHeld;
	
	/**
	 * the email of the customer who owns the seat hold
	 */
	private final String customerEmail;
	
	/**
	 * the time when the seat hold will expire
	 */
	private final LocalDateTime holdExpiration;

	/**
	 * instantiation of the seat hold
	 * @param seatsHeld the seats held
	 * @param customerEmail the email of the customer
	 * @param holdExpiration the time when the seat hold will expire
	 */
	public SeatHold(List<Seat> seatsHeld, String customerEmail, LocalDateTime holdExpiration) {
		this.seatHoldId = nextSeatHoldId;
		nextSeatHoldId++;
		this.seatsHeld = seatsHeld;
		this.customerEmail = customerEmail;
		this.holdExpiration = holdExpiration;
	}

	/**
	 * @return whether the seat hold is expired
	 */
	public boolean isExpired(){
		if(LocalDateTime.now().isAfter(holdExpiration)){
			return true;
		}
		return false;
	}
	
	/**
	 * @return the number of seconds until seat hold expiration truncated to the nearest second
	 */
	public String secondsToExpiration(){
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
