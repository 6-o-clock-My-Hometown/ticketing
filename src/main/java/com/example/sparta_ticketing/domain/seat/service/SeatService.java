package com.example.sparta_ticketing.domain.seat.service;

import com.example.sparta_ticketing.common.exception.InvalidRequestException;
import com.example.sparta_ticketing.domain.seat.dto.request.ChangeSeatRequest;
import com.example.sparta_ticketing.domain.seat.dto.response.SeatResponse;
import com.example.sparta_ticketing.domain.seat.entity.Seat;
import com.example.sparta_ticketing.domain.seat.repository.SeatRepository;
import com.example.sparta_ticketing.domain.show.entity.Show;
import com.example.sparta_ticketing.domain.show.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final ShowService showService;

    @Transactional(readOnly = true)
    public List<SeatResponse> findAllByShowId(Long showId, Pageable pageable) {
        return seatRepository.findAllByShowId(showId, pageable)
                .map(SeatResponse::toDto)
                .getContent();
    }

    @Transactional
    public void updateSeat(Long userId, Long showId, Long seatId, ChangeSeatRequest request) {

        Show show = showService.getShow(showId);
        Seat seat = seatRepository.findByIdAndUserId(seatId, userId).orElseThrow(()-> new InvalidRequestException("잘못된 정보입니다."));
        seat.updateSeat(request.getName(), request.getCount(), request.getPrice());

        changeTotalSeatCount(show);
    }

    private void changeTotalSeatCount(Show show) {
        show.sumSeat(seatRepository.sumSeatCountByShowId(show.getId()));
    }

}
