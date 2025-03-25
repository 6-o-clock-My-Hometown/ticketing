package com.example.sparta_ticketing.domain.show.entity;

import com.example.sparta_ticketing.common.entity.BaseEntity;
import com.example.sparta_ticketing.domain.show.dto.request.UpdateShowRequestDto;
import com.example.sparta_ticketing.domain.show.enums.Category;
import com.example.sparta_ticketing.domain.show.enums.Region;
import com.example.sparta_ticketing.domain.show.enums.ShowStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.example.sparta_ticketing.domain.show.enums.ShowStatus.DELETED;

@Getter
@Entity
@Table(name = "shows")
@NoArgsConstructor
public class Show extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String content;

    @Enumerated(EnumType.STRING)
    private Region region;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private LocalDateTime reservationStartDate;
    private LocalDateTime reservationEndDate;

    private int totalSeats;

    private ShowStatus isDeleted;

    public void updateShow(UpdateShowRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.category = requestDto.getCategory();
        this.content = requestDto.getContent();
        this.region = requestDto.getRegion();
        this.startDate = requestDto.getStartDate();
        this.endDate = requestDto.getEndDate();
        this.reservationStartDate = requestDto.getReservationStartDate();
        this.reservationEndDate = requestDto.getReservationEndDate();
    }

    public void deleteShow() {
        this.isDeleted = DELETED;
    }
}
