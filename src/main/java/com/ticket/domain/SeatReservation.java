package com.ticket.domain;

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
 * describes the information associated with a seat reservation placed by a customer
 */
@Entity
public class SeatReservation {
	public static final Logger LOGGER = LoggerFactory.getLogger(SeatReservation.class);

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer seatReservationId;
	
	/**
	 * the next confirmation id to be give out
	 */
	private static String nextConfirmationId = "VX3529";
	
	/**
	 * the customer email
	 */
	private String customerEmail;
	
	/**
	 * the confirmation id
	 */
	private String confirmationId;
	
	/**
	 * the venue where the seat reservation sits
	 */
	@ManyToOne
	private Venue venue;
	
	/**
	 * the list of seats that are associated with this reservation
	 */
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "seatReservation")
	private List<Seat> reservedSeats;

	/**
	 * instantiation of the seat reservation
	 * @param customerEmail
	 * @param reservedSeats
	 */
	public SeatReservation(String customerEmail, List<Seat> reservedSeats) {
		this.confirmationId = nextConfirmationId;
		updateNextConfirmationId();
		this.customerEmail = customerEmail;
		this.reservedSeats = reservedSeats;
	}
	
	public SeatReservation(){}

	/**
	 * method to update the next confirmation id for the next time a seat reservation is instantiated
	 */
	private void updateNextConfirmationId() {
		nextConfirmationId = "VX" + (Integer.parseInt(nextConfirmationId.substring(2)) + 1);
	}

	/**
	 * @return the nextConfirmationId
	 */
	public static String getNextConfirmationId() {
		return nextConfirmationId;
	}

	/**
	 * @return the customer email
	 */
	public String getCustomerEmail() {
		return customerEmail;
	}

	/**
	 * @return the confirmation id
	 */
	public String getConfirmationId() {
		return confirmationId;
	}

	/**
	 * @return the list of reserved seats
	 */
	public List<Seat> getReservedSeats() {
		return reservedSeats;
	}

}
