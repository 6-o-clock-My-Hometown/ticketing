package com.example.sparta_ticketing.domain.viewCount.repository;

import com.example.sparta_ticketing.domain.show.entity.Show;
import com.example.sparta_ticketing.domain.user.entity.User;
import com.example.sparta_ticketing.domain.viewCount.entity.ViewCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViewCountRepository extends JpaRepository<ViewCount, Long> {
    boolean existsByUserAndShow(User user, Show show);
    long countByShow(Show show);
}