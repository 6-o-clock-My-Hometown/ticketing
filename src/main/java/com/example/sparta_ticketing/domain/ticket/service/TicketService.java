package com.example.sparta_ticketing.domain.ticket.service;

import com.example.sparta_ticketing.common.exception.InvalidRequestException;
import com.example.sparta_ticketing.common.exception.UserNotFoundException;
import com.example.sparta_ticketing.common.redis.RedisLockRepository;
import com.example.sparta_ticketing.common.redis.RedisLockService;
import com.example.sparta_ticketing.common.redis.RedisService;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserService userService;
    private final ShowService showService;
    private final SeatService seatService;
    private final RedisService redisService;
    private final RedisLockService redisLockService;

    @Transactional
    public TicketResponse reserveSeat(Long userId, CreateSeatReservationRequest request) {
        User user = userService.findById(userId)
                .orElseThrow(()
                        -> new UserNotFoundException("회원 정보가 존재하지 않습니다."));

        Show show = showService.getShow(request.getShowId());
        Seat seat = seatService.getSeat(request.getSeatId(), request.getShowId());

        String remainSeatKey = "show:" + show.getId() + ":seat:" + seat.getId();
        String lockKey = "lock:" + remainSeatKey;

        Ticket ticket = redisLockService.executeWithLock(lockKey,
                () -> reserveTicket(user, seat, show, remainSeatKey)
        );

        return TicketResponse.toDto(ticket);
    }

    @Transactional
    public TicketResponse reserveSeatWithoutLock(Long userId, CreateSeatReservationRequest request) {
        User user = userService.findById(userId)
                .orElseThrow(()
                        -> new UserNotFoundException("회원 정보가 존재하지 않습니다."));

        Show show = showService.getShow(request.getShowId());
        Seat seat = seatService.getSeat(request.getSeatId(), request.getShowId());

        String remainSeatKey = "show:"+ show.getId() + ":seat:" + seat.getId();
        Long remain = redisService.decrement(remainSeatKey);

        if(remain == null){
            throw new InvalidRequestException("좌석 정보가 없습니다.");
        }

        if(remain < 0){
            throw new InvalidRequestException("해당 좌석 등급은 매진되었습니다.");
        }

        Ticket ticket = reserveTicket(user, seat, show, remainSeatKey);
        return TicketResponse.toDto(ticket);
    }


    @Transactional(readOnly = true)
    public TicketResponse getReserve(Long userId, Long id) {
        return TicketResponse.toDto(ticketRepository.findByIdAndUserId(id, userId).orElseThrow(() -> new InvalidRequestException("예매 정보가 존재하지 않습니다.")));
    }

    private Ticket reserveTicket(User user, Seat seat, Show show, String key) {

            Long remain = redisService.decrement(key);

            if(remain == null){
                throw new InvalidRequestException("좌석 정보가 없습니다.");
            }

            if(remain < 0){
                throw new InvalidRequestException("해당 좌석 등급은 매진되었습니다.");
            }

            return ticketRepository.save(new Ticket(user, seat, show));
    }
}
