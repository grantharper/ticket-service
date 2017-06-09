package com.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ticket.domain.SeatReservation;

public interface SeatReservationRepository extends JpaRepository<SeatReservation, Integer>{

}
