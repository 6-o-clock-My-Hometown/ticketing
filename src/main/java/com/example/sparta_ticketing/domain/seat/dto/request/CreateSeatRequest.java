package com.example.sparta_ticketing.domain.seat.dto.request;

import com.example.sparta_ticketing.domain.seat.enums.SeatEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSeatRequest {
    @NotBlank
    private SeatEnum name;
    @NotNull
    private int count;
    @NotNull
    private int price;
}