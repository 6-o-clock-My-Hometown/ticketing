package com.example.sparta_ticketing.domain.ticket.service;

import com.example.sparta_ticketing.common.redis.RedisService;
import com.example.sparta_ticketing.domain.auth.entity.AuthUser;
import com.example.sparta_ticketing.domain.seat.entity.Seat;
import com.example.sparta_ticketing.domain.seat.enums.SeatEnum;
import com.example.sparta_ticketing.domain.show.dto.request.CreateShowRequestDto;
import com.example.sparta_ticketing.domain.show.dto.request.UpdateShowRequestDto;
import com.example.sparta_ticketing.domain.show.entity.Show;
import com.example.sparta_ticketing.domain.show.enums.Category;
import com.example.sparta_ticketing.domain.show.enums.Region;
import com.example.sparta_ticketing.domain.show.enums.ShowStatus;
import com.example.sparta_ticketing.domain.show.repository.ShowRepository;
import com.example.sparta_ticketing.domain.ticket.entity.Ticket;
import com.example.sparta_ticketing.domain.ticket.enums.TicketStatus;
import com.example.sparta_ticketing.domain.ticket.repository.TicketRepository;
import com.example.sparta_ticketing.domain.user.entity.User;
import com.example.sparta_ticketing.domain.user.enums.UserRole;
import com.example.sparta_ticketing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
class TicketServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ShowRepository showRepository;

    @Mock
    TicketRepository ticketRepository;

    @Mock
    RedisService redisService;

    @InjectMocks
    TicketService ticketService;

    @Test
    void 예매한_좌석을_취소한다() {
        // given
        Long userId = 1L;
        User user = createUser("user", userId);

        Long showId = 1L;
        Show show = createShow(user, showId);

        Long seatId = 1L;
        Seat seat = createSeat(show, seatId);

        Long ticketId = 1L;
        Ticket ticket = createTicket(user, seat, show, ticketId);


        given(ticketRepository.getTicketIdAndUserId(anyLong(), anyLong())).willReturn(Optional.of(ticket));

        //when
        ticketService.cancelReserveSeat(userId, ticketId);

        // then
        assertEquals(TicketStatus.CANCELED, ticket.getStatus());
    }

    private User createUser(String email, Long userId) {
        User user = new User(email, "pw", "이름", "010", "1990-01-01", UserRole.ROLE_USER);
        ReflectionTestUtils.setField(user, "id", userId);

        return user;
    }

    private Show createShow(User user, Long showId) {
        Show show = new Show(
                new CreateShowRequestDto("공연", Category.MUSICAL, "내용", Region.SEOUL,
                        LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                        LocalDateTime.now(), LocalDateTime.now().plusDays(1)),
                50, user);

        ReflectionTestUtils.setField(show, "id", showId);

        return show;
    }

    private Seat createSeat(Show show, Long seatId) {
        Seat seat = new Seat(show, SeatEnum.S, 50, 100000);
        ReflectionTestUtils.setField(show, "id", seatId);
        redisService.set("ticket:show:" + show.getId() + ":seat:" + seat.getId(), String.valueOf(seat.getCount()));
        return seat;
    }

    private Ticket createTicket(User user, Seat seat, Show show, Long ticketId) {
        Ticket ticket = new Ticket(user, seat, show, TicketStatus.PURCHASED);
        ReflectionTestUtils.setField(show, "id", ticketId);
        return ticket;
    }

    private AuthUser createAuthUser(User user) {
        return new AuthUser(user.getId(), user.getEmail(), user.getUserRole());
    }
}