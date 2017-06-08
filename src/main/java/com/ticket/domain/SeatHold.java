package com.ticket.domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * describes the information associated with a seat hold placed by a customer
 */
@Entity
public class SeatHold {

	public static final Logger LOGGER = LoggerFactory.getLogger(SeatHold.class);
	
	/**
	 * the unique id for the seat hold
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long seatHoldId;
	
	/**
	 * the list of seats that are held by the seat hold
	 */
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "seatHold")
	private List<Seat> seatsHeld;
	
	/**
	 * the email of the customer who owns the seat hold
	 */
	private final String customerEmail;
	
	/**
	 * the time when the seat hold will expire
	 */
	private LocalDateTime expireTime;
	
	/**
	 * flag to indicate that the hold is in progress
	 */
	private boolean inProgress;
	
	/**
	 * the venue where the seat hold is located
	 */
	@ManyToOne
	private Venue venue;
	
	/**
	 * instantiation of the seat hold
	 * @param seatsHeld the seats held
	 * @param customerEmail the email of the customer
	 * @param holdExpiration the time when the seat hold will expire
	 */
	public SeatHold(String customerEmail, Venue venue){
		this.venue = venue;
		this.customerEmail = customerEmail;
		this.inProgress = true;
	}
	
	/**
	 * commits the seat hold and establishes the time when it will expire
	 * @param seatsHeld the seats held
	 * @param holdExpiration the time when the seat hold will expire
	 */
	public void commitSeatHold(LocalDateTime expireTime){
		this.expireTime = expireTime;
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
		if(this.expireTime != null){
			this.expireTime = null;
		}
	}

	
	// TODO Move this out of this method
	/**
	 * @return whether the seat hold is currently holding the seats
	 */
	public boolean isHolding(){
		if(this.inProgress){
			return true;
		}
		else 
		if(expireTime != null && LocalDateTime.now().isBefore(expireTime)){
			return true;
		}
		return false;
	}
	
	/**
	 * @return the number of seconds until seat hold expiration truncated to the nearest second
	 */
	public String printSecondsToExpiration(){
		Duration timeToExpire = Duration.between(LocalDateTime.now(), expireTime);
		if(timeToExpire.toMillis() > 0){
			return "" + (timeToExpire.toMillis() / 1000) + " seconds";
		}
		return "0 seconds";
		
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




}
