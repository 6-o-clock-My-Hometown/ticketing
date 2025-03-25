package com.example.sparta_ticketing.domain.show.repository;

import com.example.sparta_ticketing.domain.show.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowRepository extends JpaRepository<Show, Long> {
}
