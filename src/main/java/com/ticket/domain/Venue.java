package com.ticket.domain;

import java.time.Duration;

/**
 * The venue object will describe a venue with a given number of rows of seats
 * It will allow for seat reservations
 *
 */
public class Venue {
	
	private final Duration holdDuration;
	
	public Venue(int holdSeconds){
		this.holdDuration = Duration.ofSeconds(holdSeconds);
	}
	
	public Duration getHoldDuration(){
		return holdDuration;
	}
	
	

}
