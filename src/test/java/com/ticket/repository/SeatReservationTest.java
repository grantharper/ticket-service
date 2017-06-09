package com.ticket.repository;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ticket.App;
import com.ticket.console.TicketUserInterface;
import com.ticket.domain.SeatReservation;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
public class SeatReservationTest {
	
	@MockBean
	private TicketUserInterface ticketUserInterface;
	
	@Resource
	private SeatReservationRepository seatReservationRepository;

	private String confirmationId;
	private SeatReservation seatReservation;

	@Before
	public void setUp() {
		seatReservation = new SeatReservation("email@email.com");
		confirmationId = seatReservation.getConfirmationId();
		seatReservationRepository.save(seatReservation);
	}

	@Test
	public void testGetByConfirmationCode() {
		assertEquals(seatReservation.getSeatReservationId(),
				seatReservationRepository.getReservationByConfirmationId(confirmationId).getSeatReservationId());
	}

}
