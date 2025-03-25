package com.example.sparta_ticketing.domain.show.dto.response;

import com.example.sparta_ticketing.domain.show.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShowResponseDto {
    private String title;

    private Category category;

    private String content;

    private String region;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private LocalDateTime reservationStartDate;

    private LocalDateTime reservationEndDate;

    private int totalSeat;

    private String imageUrl;


}
