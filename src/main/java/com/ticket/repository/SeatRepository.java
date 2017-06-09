package com.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ticket.domain.Seat;

public interface SeatRepository extends JpaRepository<Seat, Integer>{

}
