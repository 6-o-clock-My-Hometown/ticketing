package com.example.sparta_ticketing.domain.seat.service;

import com.example.sparta_ticketing.common.redis.RedisService;
import com.example.sparta_ticketing.domain.seat.dto.request.ChangeSeatRequest;
import com.example.sparta_ticketing.domain.seat.dto.response.SeatResponse;
import com.example.sparta_ticketing.domain.seat.entity.Seat;
import com.example.sparta_ticketing.domain.seat.enums.SeatEnum;
import com.example.sparta_ticketing.domain.seat.repository.SeatRepository;
import com.example.sparta_ticketing.domain.show.entity.Show;
import com.example.sparta_ticketing.domain.show.repository.ShowRepository;
import com.example.sparta_ticketing.domain.show.service.ShowService;
import com.example.sparta_ticketing.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SeatServiceTest {


    //show
    public static final Long TEST_SHOW_ID = 3L;
    public static final Long TEST_INVALID_SHOW_ID = 9999L;
    public static final Show TEST_SHOW = new Show();

    //seat
    public static final Long TEST_SEAT_ID = 7L;
    public static final Long TEST_INVALID_SEAT_ID = 9999L;
    public static final Seat TEST_SEAT = new Seat();

    //user
    public static final Long TEST_ROLE_DIRECTOR_ID = 2L;
    private static final Long TEST_INVALID_ROLE_DIRECTOR = 9999L;
    public static final User TEST_ROLE_DIRECTOR = new User();

    //페이징
    public static final Pageable TEST_PAGEABLE = PageRequest.of(1, 10);

    //dto
    public static final ChangeSeatRequest TEST_CHANGE_SEAT_REQUEST = new ChangeSeatRequest(SeatEnum.SVIP, TEST_SEAT.getCount() + 10, TEST_SEAT.getPrice() + 10);

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private ShowRepository showRepository;

    @Mock
    private ShowService showService;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private SeatService seatService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(TEST_ROLE_DIRECTOR, "id", TEST_ROLE_DIRECTOR_ID);
        ReflectionTestUtils.setField(TEST_SHOW, "id", TEST_SHOW_ID);
        ReflectionTestUtils.setField(TEST_SEAT, "id", TEST_SEAT_ID);
        ReflectionTestUtils.setField(TEST_SEAT, "show", TEST_SHOW);
    }

    @Test
    void 공연_아이디로_모든_좌석_조회_성공() {
        //given
        ArrayList<Seat> seats = new ArrayList<>();
        seats.add(TEST_SEAT);

        PageImpl<Seat> seatsList = new PageImpl<>(seats);
        given(seatRepository.findAllByShowId(TEST_SHOW_ID, TEST_PAGEABLE)).willReturn(seatsList);

        //when
        List<SeatResponse> findSeats = seatService.findAllByShowId(TEST_SHOW_ID, TEST_PAGEABLE);

        //then
        assertThat(findSeats).isNotNull();
        assertThat(findSeats.get(0).getShowId()).isEqualTo(TEST_SHOW_ID);
    }

    @Test
    void 공연_아이디로_좌석_조회시_빈_배열_응답() {
        //given
        ArrayList<Seat> seats = new ArrayList<>();
        PageImpl<Seat> seatList = new PageImpl<>(seats);

        given(seatRepository.findAllByShowId(TEST_INVALID_SHOW_ID, TEST_PAGEABLE)).willReturn(seatList);
        //when

        List<SeatResponse> findSeats = seatService.findAllByShowId(TEST_INVALID_SHOW_ID, TEST_PAGEABLE);

        //then
        assertThat(findSeats.size()).isEqualTo(0);
    }

}