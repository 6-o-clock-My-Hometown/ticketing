package com.example.sparta_ticketing.domain.ticket.dto.request;

import com.example.sparta_ticketing.domain.seat.entity.Seat;
import lombok.Getter;

@Getter
public class CreateSeatReservationRequest {
    private Long seatId;
    private Long showId;
}
