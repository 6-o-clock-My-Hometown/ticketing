package com.example.sparta_ticketing.domain.seat.dto.response;

import com.example.sparta_ticketing.domain.seat.entity.Seat;
import com.example.sparta_ticketing.domain.seat.enums.SeatEnum;
import com.example.sparta_ticketing.domain.show.entity.Show;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeatResponse {
    private Long id;
    private Long showId;
    private SeatEnum name;
    private int count;
    private int price;


    public static SeatResponse toDto(Seat seat){
        return new SeatResponse(
                seat.getId(),
                seat.getShow().getId(),
                seat.getName(),
                seat.getCount(),
                seat.getPrice());
    }

}
