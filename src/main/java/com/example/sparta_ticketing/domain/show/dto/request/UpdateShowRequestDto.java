package com.example.sparta_ticketing.domain.show.dto.request;

import com.example.sparta_ticketing.domain.show.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateShowRequestDto {

    private String title;

    private Category category;

    private String content;

    private String region;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDate reservationStartDate;

    private LocalDate reservationEndDate;

    private int totalSeat;

    private String imageUrl;
}
