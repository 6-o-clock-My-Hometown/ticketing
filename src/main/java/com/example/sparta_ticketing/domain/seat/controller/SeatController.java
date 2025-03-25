package com.example.sparta_ticketing.domain.seat.controller;

import com.example.sparta_ticketing.domain.auth.entity.AuthUser;
import com.example.sparta_ticketing.domain.seat.dto.request.ChangeSeatRequest;
import com.example.sparta_ticketing.domain.seat.dto.response.SeatResponse;
import com.example.sparta_ticketing.domain.seat.entity.Seat;
import com.example.sparta_ticketing.domain.seat.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @GetMapping("/{showId}/seats/")
    public ResponseEntity<List<SeatResponse>> getSeat(@PathVariable Long showId, Pageable pageable) {
        return ResponseEntity.ok(seatService.findAllByShowId(showId, pageable));
    }

    @PatchMapping("/{showId}/seats/{seatsId}")
    public ResponseEntity<Void> updateSeat(@AuthenticationPrincipal AuthUser auther,
                                           @PathVariable Long showId,
                                           @PathVariable Long seatsId,
                                           @RequestBody ChangeSeatRequest request) {

        seatService.updateSeat(auther.getId(), showId, seatsId, request);
        return ResponseEntity.ok().build();
    }

}
