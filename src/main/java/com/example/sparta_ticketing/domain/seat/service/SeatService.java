package com.example.sparta_ticketing.domain.seat.service;

import com.example.sparta_ticketing.domain.seat.dto.response.SeatResponse;
import com.example.sparta_ticketing.domain.seat.entity.Seat;
import com.example.sparta_ticketing.domain.seat.repository.SeatRepository;
import com.example.sparta_ticketing.domain.show.dto.request.CreateShowSeatsRequestDto;
import com.example.sparta_ticketing.domain.show.entity.Show;
import com.example.sparta_ticketing.domain.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


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



    public void saveSeats(Show show, List<CreateShowSeatsRequestDto> seatDto) {
        List<Seat> seats = seatDto.stream()
                .map(dto -> new Seat(show, dto.getSeatName(), dto.getSeatCount(), dto.getSeatPrice()))
                .collect(Collectors.toList());

        seatRepository.saveAll(seats);
    }

}
