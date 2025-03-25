package com.example.sparta_ticketing.domain.seat.service;

import com.example.sparta_ticketing.domain.seat.dto.response.SeatResponse;
import com.example.sparta_ticketing.domain.seat.entity.Seat;
import com.example.sparta_ticketing.domain.seat.repository.SeatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SeatService {

    private SeatRepository seatRepository;

    @Transactional
    public List<SeatResponse> findAllByShowId(Long showId, Pageable pageable) {
        return seatRepository.findAllByShowId(showId, pageable)
                .map(SeatResponse::toDto)
                .getContent();
    }

}
