package com.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ticket.domain.Venue;

public interface VenueRepository extends JpaRepository<Venue, Integer>{

}
