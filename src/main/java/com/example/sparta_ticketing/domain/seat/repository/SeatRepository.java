package com.example.sparta_ticketing.domain.seat.repository;

import com.example.sparta_ticketing.domain.seat.entity.Seat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    Page<Seat> findAllByShowId(Long showId, Pageable pageable);

    @Query("select s from Seat s join s.show sh where s.id = :seatId and sh.userId = :userId")
    Optional<Seat> findByIdAndUserId(@Param("seatId") Long id, @Param("userId") Long userId);

    @Query("select sum(s.count) from Seat s where s.show= :showId")
    int sumSeatCountByShowId(@Param("showId") Long showId);
}
