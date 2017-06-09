package com.ticket.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Describes a seat in a venue and its state regarding whether it is available, on hold, or reserved
 *
 */
@Entity
public class Seat {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(Seat.class);
	
	/**
	 * the unique identifier for the seat
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer seatId;
	
	
	/**
	 * the user-friendly seat number in the row
	 */
	private Integer seatNumber;
	
	/**
	 * the seat hold associated with this seat
	 */
	@ManyToOne
	private SeatHold seatHold;
	
	/**
	 * seat reservation for the seat object
	 */
	@ManyToOne
	private SeatReservation seatReservation;
	
	/**
	 * row where the seat is located
	 */
	@ManyToOne
	private Row row;
	
	protected Seat(){}
	
	/**
	 * Instantiation of the seat
	 * 
	 * @param seatRow
	 *            the row where the seat is located
	 * @param seatNum
	 *            the number of the seat
	 */
	public Seat(Integer seatNum, Row row) {
		this.seatNumber = seatNum;
		this.row = row;
	}

	/**
	 * determines whether a seat is available for being held or reserved
	 * 
	 * @return an indicator as to whether the seat is available
	 */
	public boolean isAvailable() {
		if (isReserved()) {
			return false;
		} else if (isHeld()) {
			return false;
		}
		return true;
	}

	/**
	 * inspects the hold time on the seat, if present, to determine whether the
	 * hold has expired and the seat is available again
	 * 
	 * @return an indicator as to whether the seat is held
	 */
	public boolean isHeld() {
		if (seatHold != null && seatHold.isHolding()){
			return true;
		}
		return false;
	}

	/**
	 * code for the seat being available
	 */
	public static final String SEAT_AVAILABLE_CODE = "A";
	
	/**
	 * code for the seat as held
	 */
	public static final String SEAT_HELD_CODE = "H";

	/**
	 * code for the seat being available
	 */
	public static final String SEAT_RESERVED_CODE = "R";
	
	/**
	 * 
	 * @return string representation of the state of the seat
	 */
	public String print() {
		if (this.isReserved()) {
			return SEAT_RESERVED_CODE;
		} else if (this.isHeld()) {
			return SEAT_HELD_CODE;
		} else {
			return SEAT_AVAILABLE_CODE;
		}
	}
	
	/**
	 * places a hold on the seat by establishing the time when the seat was held
	 * @return time when the seat was held
	 */
	public void placeHold(SeatHold seatHold) {
		this.seatHold = seatHold;
	}

	/**
	 * reserves the seat
	 * @param seatReservation the reservation on the seat
	 */
	public void reserveSeat(SeatReservation seatReservation) {
		this.seatReservation = seatReservation;
	}

	/**
	 * @return the seatId
	 */
	public Integer getSeatId() {
		return seatId;
	}

	/**
	 * @param seatId the seatId to set
	 */
	public void setSeatId(Integer seatId) {
		this.seatId = seatId;
	}

	/**
	 * @return the seatHold
	 */
	public SeatHold getSeatHold() {
		return seatHold;
	}

	/**
	 * @param seatHold the seatHold to set
	 */
	public void setSeatHold(SeatHold seatHold) {
		this.seatHold = seatHold;
	}

	/**
	 * @return the reserved
	 */
	public boolean isReserved() {
		if(seatReservation != null){
			return true;
		}
		return false;
	}

//	/**
//	 * @return the customerReservationEmail
//	 */
//	public String getCustomerReservationEmail() {
//		return customerReservationEmail;
//	}
//
//	/**
//	 * @param customerReservationEmail the customerReservationEmail to set
//	 */
//	public void setCustomerReservationEmail(String customerReservationEmail) {
//		this.customerReservationEmail = customerReservationEmail;
//	}

	/**
	 * @return the row
	 */
	public Row getRow() {
		return row;
	}

	/**
	 * @param row the row to set
	 */
	public void setRow(Row row) {
		this.row = row;
	}
	
	

	/**
	 * @return the seatNumber
	 */
	public Integer getSeatNumber() {
		return seatNumber;
	}

	/**
	 * @param seatNumber the seatNumber to set
	 */
	public void setSeatNumber(Integer seatNumber) {
		this.seatNumber = seatNumber;
	}
	
	

	/**
	 * @return the seatReservation
	 */
	public SeatReservation getSeatReservation() {
		return seatReservation;
	}

	/**
	 * @param seatReservation the seatReservation to set
	 */
	public void setSeatReservation(SeatReservation seatReservation) {
		this.seatReservation = seatReservation;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Seat [seatId=" + seatId + ", seatNumber=" + seatNumber + ", seatHold=" + seatHold + ", seatReservation="
				+ seatReservation + ", row=" + row + "]";
	}

	

}
