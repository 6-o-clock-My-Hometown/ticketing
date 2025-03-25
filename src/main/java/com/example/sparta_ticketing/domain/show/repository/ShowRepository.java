package com.example.sparta_ticketing.domain.show.repository;

import com.example.sparta_ticketing.domain.show.entity.Show;
import com.example.sparta_ticketing.domain.show.enums.ShowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ShowRepository extends JpaRepository<Show, Long> {
    Page<Show> findByStatus(ShowStatus showStatus, Pageable pageable);

    @Query("SELECT s FROM Show s WHERE s.id = :showId AND s.status = 'NOT_DELETED'")
    Optional<Show> findShowById(@Param("showId") Long showId);
}
