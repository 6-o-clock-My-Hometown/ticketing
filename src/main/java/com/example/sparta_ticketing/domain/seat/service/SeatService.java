package com.example.sparta_ticketing.domain.seat.service;

import com.example.sparta_ticketing.common.exception.InvalidRequestException;
import com.example.sparta_ticketing.domain.seat.dto.request.ChangeSeatRequest;
import com.example.sparta_ticketing.domain.seat.dto.response.SeatResponse;
import com.example.sparta_ticketing.domain.seat.entity.Seat;
import com.example.sparta_ticketing.domain.seat.repository.SeatRepository;
import com.example.sparta_ticketing.domain.show.service.ShowService;
import com.example.sparta_ticketing.domain.show.entity.Show;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final ShowService showService;
    private final StringRedisTemplate redisTemplate;

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
        redisTemplate.opsForValue().set("show:"+ seat.getShow().getId() + ":seat:" + seat.getId(),String.valueOf(seat.getCount()));
    }

    private void changeTotalSeatCount(Show show) {
        show.sumSeat(seatRepository.sumSeatCountByShowId(show.getId()));

    }


    public Seat getSeat(Long showId, Long seatId) {
        Show show = showService.getShow(showId);
        return seatRepository.findByIdAndShowId(seatId, show.getId()).orElseThrow(() -> new InvalidRequestException("조회된 좌석 정보가 없습니다."));
    }

//     public void saveSeats(Show show, List<CreateShowSeatsRequestDto> seatDto) {
//         List<Seat> seats = seatDto.stream()
//                 .map(dto -> new Seat(show, dto.getSeatName(), dto.getSeatCount(), dto.getSeatPrice()))
//                 .collect(Collectors.toList());

//         seatRepository.saveAll(seats);

//     }

}
