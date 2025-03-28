package com.example.sparta_ticketing.common.scheduler;

import com.example.sparta_ticketing.domain.seat.entity.Seat;
import com.example.sparta_ticketing.domain.seat.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TicketScheduler {

    private final SeatService seatService;

    @Scheduled(fixedRate = 30000)
    public void updateRemainingSeatsCount(){
        List<Seat> seatList= seatService.getSeatList();

        // 남은 좌석 수 저장
        for (Seat seat : seatList){
            seatService.updateSeatCountWithLock(seat.getShow().getId(), seat.getId());
        }
    }
}
