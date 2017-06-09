package com.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ticket.domain.SeatHold;

public interface SeatHoldRepository extends JpaRepository<SeatHold, Integer>{

}
