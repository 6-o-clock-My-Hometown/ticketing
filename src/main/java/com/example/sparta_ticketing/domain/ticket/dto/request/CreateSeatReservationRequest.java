package com.example.sparta_ticketing.domain.ticket.dto.request;

import com.example.sparta_ticketing.domain.seat.entity.Seat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateSeatReservationRequest {
    private Long seatId;
    private Long showId;
}
