package com.example.sparta_ticketing.domain.show.entity;

import com.example.sparta_ticketing.common.entity.Timestamped;
import com.example.sparta_ticketing.domain.show.enums.Category;
import com.example.sparta_ticketing.domain.show.enums.Region;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "shows")
@NoArgsConstructor
public class Show extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Category category;

    private String content;

    private Region region;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private LocalDateTime reservationStartDate;
    private LocalDateTime reservationEndDate;

    private int totalSeats;

}
