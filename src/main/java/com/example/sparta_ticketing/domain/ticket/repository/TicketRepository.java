package com.example.sparta_ticketing.domain.ticket.repository;

import com.example.sparta_ticketing.domain.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByIdAndUserId(Long id, Long userId);

    @Query("select s from Ticket s join User u where s.id = :id and u.id = :userId and s.status = 'PURCHASED'")
    Optional<Ticket> getTicketIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}
