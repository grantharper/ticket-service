package com.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ticket.domain.SeatReservation;

public interface SeatReservationRepository extends JpaRepository<SeatReservation, Integer>{

	@Query("select s from SeatReservation s where s.confirmationId=?1")
	public SeatReservation getReservationByConfirmationId(String confirmationId);
	
}
