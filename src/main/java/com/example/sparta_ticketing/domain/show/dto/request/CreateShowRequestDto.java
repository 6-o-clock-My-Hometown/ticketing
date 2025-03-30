package com.example.sparta_ticketing.domain.show.dto.request;

import com.example.sparta_ticketing.domain.show.enums.Category;
import com.example.sparta_ticketing.domain.show.enums.Region;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class CreateShowRequestDto {
    private String title;
    private Category category;
    private String content;
    private Region region;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime reservationStartDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime reservationEndDate;
    private List<CreateShowSeatsRequestDto> seats;

    public CreateShowRequestDto(String title, Category category, String content, Region region, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime reservationStartDate, LocalDateTime reservationEndDate) {
        this.title = title;
        this.category = category;
        this.content = content;
        this.region = region;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reservationStartDate = reservationStartDate;
        this.reservationEndDate = reservationEndDate;
    }

    public CreateShowRequestDto(String title, Category category, String content, Region region, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime reservationStartDate, LocalDateTime reservationEndDate, List<CreateShowSeatsRequestDto> seats) {
        this.title = title;
        this.category = category;
        this.content = content;
        this.region = region;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reservationStartDate = reservationStartDate;
        this.reservationEndDate = reservationEndDate;
        this.seats = seats;
    }
}
