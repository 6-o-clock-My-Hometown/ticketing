package com.example.sparta_ticketing.domain.show.repository;

import com.example.sparta_ticketing.domain.show.entity.Show;
import com.example.sparta_ticketing.domain.show.enums.ShowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShowRepository extends JpaRepository<Show, Long> {
    Page<Show> findByStatus(ShowStatus showStatus, Pageable pageable);

    @Query("SELECT s FROM Show s WHERE s.id = :showId AND s.status = :showStatus")
    Optional<Show> findShowById(@Param("showId") Long showId, @Param("showStatus")ShowStatus showStatus);

    List<Show> findByIdAfterAndEndDate(Long idAfter, LocalDateTime endDate);

    @Query("SELECT s FROM Show s WHERE s.status = :showStatus AND s.endDate < :now")
    List<Show> findExpiredShow(@Param("now") LocalDateTime now, @Param("showStatus")ShowStatus showStatus);

    List<Show> findAllByStatus(ShowStatus status);
}
