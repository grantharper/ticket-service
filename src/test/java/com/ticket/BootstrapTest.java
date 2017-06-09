package com.ticket;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.beryx.textio.TextIO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ticket.console.TicketUserInterface;
import com.ticket.domain.Venue;
import com.ticket.repository.VenueRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
public class BootstrapTest {
	
	@MockBean
	private TicketUserInterface ticketUserInterface;
	
	@Resource
	private VenueRepository venueRepository;
	
	@Value("${venue.id}")
	private Integer venueId;
	
	@Resource
	private Bootstrap bootstrap;
	
	@Test
	public void testLoadVenue() {
		
		Venue venue = venueRepository.findOne(venueId);
		assertEquals(venueId, bootstrap.getVenueId());
		assertEquals(bootstrap.getNumRows().intValue(), venue.getRows().size());
		assertEquals(bootstrap.getNumSeatsPerRow().intValue(), venue.getRows().iterator().next().getSeats().size());
	}

}
