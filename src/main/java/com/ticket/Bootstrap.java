package com.ticket;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.ticket.console.TicketUserInterface;
import com.ticket.domain.Row;
import com.ticket.domain.Seat;
import com.ticket.domain.Venue;
import com.ticket.repository.RowRepository;
import com.ticket.repository.SeatRepository;
import com.ticket.repository.VenueRepository;

@Component
public class Bootstrap implements ApplicationListener<ContextRefreshedEvent> {

	public static final Logger LOGGER = LoggerFactory.getLogger(TicketUserInterface.class);
	
	@Resource
	private VenueRepository venueRepository;
	@Resource
	private RowRepository rowRepository;
	@Resource
	private SeatRepository seatRepository;
	
	@Value("${venue.rows}")
	private Integer numRows;
	
	@Value("${venue.seatsPerRow}")
	private Integer numSeatsPerRow;
	
	@Value("${venue.id}")
	private Integer venueId;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		LOGGER.info("Loading venue");
		loadVenue();

	}
	
	private void loadVenue(){
		
		Venue venue = new Venue(venueId);
		venueRepository.save(venue);
		
		for (Integer i = 0; i < numRows; i++) {
			Row row = new Row(i + 1, venue);
			rowRepository.save(row);
			for (Integer j = 0; j < numSeatsPerRow; j++) {
				Seat seat = new Seat(j + 1, row);
				seatRepository.save(seat);
			}
		}
		
		venue = venueRepository.findOne(venueId);
		LOGGER.info("Number of rows: " + venue.getRows().size());
		
	}

	/**
	 * @return the numRows
	 */
	public Integer getNumRows() {
		return numRows;
	}

	/**
	 * @return the numSeatsPerRow
	 */
	public Integer getNumSeatsPerRow() {
		return numSeatsPerRow;
	}

	/**
	 * @return the venueId
	 */
	public Integer getVenueId() {
		return venueId;
	}
	
	
}
