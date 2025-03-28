package com.example.sparta_ticketing.domain.seat.repository;

import com.example.sparta_ticketing.domain.seat.entity.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    Page<Seat> findAllByShowId(Long showId, Pageable pageable);

    @Query("select s from Seat s join s.show sh where s.id = :seatId and sh.user.id = :userId")
    Optional<Seat> findByIdAndUserId(@Param("seatId") Long id, @Param("userId") Long userId);

    @Query("select sum(s.count) from Seat s where s.show.id= :showId")
    int sumSeatCountByShowId(@Param("showId") Long showId);

    @Query("select s from Seat s join s.show sh where s.id = :seatId and sh.id = :showId")
    Optional<Seat> findByIdAndShowId(@Param("seatId") Long seatId, @Param("showId") Long showId);


    @Query("select s.remainSeatCount from Seat s where s.id= :seatId")
    Integer remainSeatCount(@Param("seatId") Long seatId);

    @Query("select s from Seat s join s.show where s.show.status = 'NOT_DELETED'")
    List<Seat> findAllByShowStatus();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Seat s join s.show sh where s.id = :seatId and sh.id = :showId")
    Optional<Seat> findByIdAndShowIdWithPessimisticLock(@Param("seatId") Long seatId, @Param("showId") Long showId);
}
