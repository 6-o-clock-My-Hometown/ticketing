package com.example.sparta_ticketing.domain.seat.repository;

import com.example.sparta_ticketing.domain.seat.entity.Seat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    Page<Seat> findAllByShowId(Long showId, Pageable pageable);
}
