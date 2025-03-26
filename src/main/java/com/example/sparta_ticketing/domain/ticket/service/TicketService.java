package com.example.sparta_ticketing.domain.ticket.service;

import com.example.sparta_ticketing.common.exception.InvalidRequestException;
import com.example.sparta_ticketing.common.exception.UserNotFoundException;
import com.example.sparta_ticketing.domain.seat.entity.Seat;
import com.example.sparta_ticketing.domain.seat.service.SeatService;
import com.example.sparta_ticketing.domain.show.entity.Show;
import com.example.sparta_ticketing.domain.show.service.ShowService;
import com.example.sparta_ticketing.domain.ticket.dto.request.CreateSeatReservationRequest;
import com.example.sparta_ticketing.domain.ticket.dto.response.TicketResponse;
import com.example.sparta_ticketing.domain.ticket.entity.Ticket;
import com.example.sparta_ticketing.domain.ticket.repository.TicketRepository;
import com.example.sparta_ticketing.domain.user.entity.User;
import com.example.sparta_ticketing.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserService userService;
    private final ShowService showService;
    private final SeatService seatService;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public TicketResponse reserveSeat(Long userId, CreateSeatReservationRequest request) {
        User user = userService.findById(userId)
                .orElseThrow(()
                        -> new UserNotFoundException("회원 정보가 존재하지 않습니다."));

        Show show = showService.getShow(request.getShowId());
        Seat seat = seatService.getSeat(request.getSeatId(), request.getShowId());

        //redis count-1
        String remainSeatKey = "show:"+ show.getId() + ":seat:" + seat.getId();
        Long remain = redisTemplate.opsForValue().decrement(remainSeatKey);

        if(remain == null){
            throw new InvalidRequestException("좌석 정보가 없습니다.");
        }

        if(remain < 0){
            redisTemplate.opsForValue().increment(remainSeatKey);
            throw new InvalidRequestException("해당 좌석 등급은 매진되었습니다.");
        }

        Ticket ticket = ticketRepository.save(new Ticket(user, seat, show));
        return TicketResponse.toDto(ticket);
    }

    @Transactional(readOnly = true)
    public TicketResponse getReserve(Long userId, Long id) {
        return TicketResponse.toDto(ticketRepository.findByIdAndUserId(id, userId).orElseThrow(() -> new InvalidRequestException("예매 정보가 존재하지 않습니다.")));
    }
}
