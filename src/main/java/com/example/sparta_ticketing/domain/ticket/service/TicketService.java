package com.example.sparta_ticketing.domain.ticket.service;

import com.example.sparta_ticketing.common.config.DistributedLock;
import com.example.sparta_ticketing.common.config.PessimisticLock;
import com.example.sparta_ticketing.common.exception.InvalidRequestException;
import com.example.sparta_ticketing.common.exception.UserNotFoundException;
import com.example.sparta_ticketing.common.redis.RedisLockService;
import com.example.sparta_ticketing.common.redis.RedisService;
import com.example.sparta_ticketing.domain.seat.entity.Seat;
import com.example.sparta_ticketing.domain.seat.repository.SeatRepository;
import com.example.sparta_ticketing.domain.seat.service.SeatService;
import com.example.sparta_ticketing.domain.show.entity.Show;
import com.example.sparta_ticketing.domain.show.service.ShowService;
import com.example.sparta_ticketing.domain.ticket.dto.request.CreateSeatReservationRequest;
import com.example.sparta_ticketing.domain.ticket.dto.response.TicketResponse;
import com.example.sparta_ticketing.domain.ticket.entity.Ticket;
import com.example.sparta_ticketing.domain.ticket.enums.TicketStatus;
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
    private final SeatRepository seatRepository;

    @Transactional
    @DistributedLock(key = "'lock:show:' + #request.showId + ':seat:' + #request.seatId")
    // 분산락
    public TicketResponse reserveSeat(Long userId, CreateSeatReservationRequest request) {
        User user = userService.findById(userId)
                .orElseThrow(()
                        -> new UserNotFoundException("회원 정보가 존재하지 않습니다."));

        Show show = showService.getShow(request.getShowId());
        Seat seat = seatService.getSeat(request.getShowId(), request.getSeatId());

        String remainSeatKey = "ticket:show:" + show.getId() + ":seat:" + seat.getId();

        if(!redisService.exists("canReserve:show:"+show.getId())) {
            throw new InvalidRequestException("예매 가능 기간이 지났습니다.");
        }

        Ticket ticket = reserveTicket(user, seat, show, remainSeatKey);
        return TicketResponse.toDto(ticket);

    }

    // 락을 사용안했을 때
    @Transactional
    public TicketResponse reserveSeatWithoutLock(Long userId, CreateSeatReservationRequest request) {
        User user = userService.findById(userId)
                .orElseThrow(()
                        -> new UserNotFoundException("회원 정보가 존재하지 않습니다."));

        Show show = showService.getShow(request.getShowId());
        Seat seat = seatService.getSeat(request.getSeatId(), request.getShowId());
        int remainSeatCount = seatService.countRemainSeats(request.getSeatId());

        if(remainSeatCount < 0){
            throw new InvalidRequestException("잔여 좌석이 없습니다.");
        }

        Ticket ticket = new Ticket(user, seat, show, TicketStatus.PURCHASED);
        ticketRepository.save(ticket);

        seat.updateRemainSeat(remainSeatCount-1);
        seatRepository.save(seat);

        return TicketResponse.toDto(ticket);
    }

    //비관적락
//    @Transactional
//    public TicketResponse reserveSeatWithPessimisticLock(Long userId, CreateSeatReservationRequest request){
//        User user = userService.findById(userId)
//                .orElseThrow(()
//                        -> new UserNotFoundException("회원 정보가 존재하지 않습니다."));
//
//        Show show = showService.getShow(request.getShowId());
//        Seat seat = seatService.findByIdAndShowIdWithPessimisticLock(request.getShowId(), request.getSeatId());
//
//        if(seat.getRemainSeatCount() < 1){
//            throw new InvalidRequestException("잔여 좌석이 없습니다.");
//        }
//
//        Ticket ticket = new Ticket(user, seat, show, TicketStatus.PURCHASED);
//        ticketRepository.save(ticket);
//
//        seat.updateRemainSeat(seat.getRemainSeatCount()-1);
//        seatRepository.save(seat);
//
//        return TicketResponse.toDto(ticket);
//    }

    @Transactional
    @PessimisticLock(key = "'PessimisticLock:show:' + #request.showId + ':seat:' + #request.seatId")
    public TicketResponse reserveSeatWithPessimisticLock(Long userId, CreateSeatReservationRequest request) {
        User user = userService.findById(userId)
                .orElseThrow(()
                        -> new UserNotFoundException("회원 정보가 존재하지 않습니다."));

        Show show = showService.getShow(request.getShowId());
        Seat seat = seatService.getSeat(request.getShowId(), request.getSeatId());

        String remainSeatKey = "ticket:show:" + show.getId() + ":seat:" + seat.getId();

        if(!redisService.exists("canReserve:show:"+show.getId())) {
            throw new InvalidRequestException("예매 가능 기간이 지났습니다.");
        }

        Ticket ticket = reserveTicket(user, seat, show, remainSeatKey);
        return TicketResponse.toDto(ticket);

    }


    @Transactional
    public void cancelReserveSeat(Long userId, Long ticketId) {
        Ticket ticket = ticketRepository.getTicketIdAndUserId(ticketId, userId).orElseThrow(() -> new InvalidRequestException("예매 정보가 존재하지 않습니다."));
        String remainSeatKey = "ticket:show:" + ticket.getShow().getId() + ":seat:" + ticket.getSeat().getId();
        ticket.cancelTicket();

        redisService.increment(remainSeatKey);
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

            return ticketRepository.save(new Ticket(user, seat, show, TicketStatus.PURCHASED));
    }
}
