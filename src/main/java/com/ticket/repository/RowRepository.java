package com.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ticket.domain.Row;

public interface RowRepository extends JpaRepository<Row, Integer>{

}
