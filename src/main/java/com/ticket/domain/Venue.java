package com.ticket.domain;

import java.util.LinkedHashSet;
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
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long venueId;

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
	 * @param numRows
	 *            number of rows in the venue
	 * @param numSeatsPerRow
	 *            number of seats per row in the venue
	 */
	public Venue(final Long numRows, final Long numSeatsPerRow) {
		for (Long i = 0L; i < numRows; i++) {
			this.rows.add(new Row(i + 1, numSeatsPerRow, this));
		}
	}

	/**
	 * @return the venueId
	 */
	public Long getVenueId() {
		return venueId;
	}

	/**
	 * @param venueId the venueId to set
	 */
	public void setVenueId(Long venueId) {
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
		return "Venue [venueId=" + venueId + ", rows=" + rows + ", seatHolds=" + seatHolds + ", seatReservations="
				+ seatReservations + "]";
	}

	
	
	
}
