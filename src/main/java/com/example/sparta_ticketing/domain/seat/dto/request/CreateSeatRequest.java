package com.example.sparta_ticketing.domain.seat.dto.request;

import com.example.sparta_ticketing.domain.seat.enums.SeatEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSeatRequest {
    private SeatEnum name;
    private int count;
    private int price;
}
