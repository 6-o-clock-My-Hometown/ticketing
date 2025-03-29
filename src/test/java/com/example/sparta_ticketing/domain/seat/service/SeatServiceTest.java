package com.example.sparta_ticketing.domain.seat.service;

import com.example.sparta_ticketing.common.exception.InvalidRequestException;
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
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SeatServiceTest {
    //show
    public static final Long TEST_SHOW_ID = 3L;
    public static final Long TEST_INVALID_SHOW_ID = 9999L;
    public static final Show TEST_SHOW = new Show();

    //seat
    public static final Long TEST_SEAT_ID = 7L;
    public static final Long TEST_INVALID_SEAT_ID = 9999L;
    public static final Seat TEST_SEAT = new Seat(TEST_SEAT_ID,TEST_SHOW, SeatEnum.SVIP, 1000, 150000, 1000);

    //user
    public static final Long TEST_ROLE_DIRECTOR_ID = 2L;
    private static final Long TEST_INVALID_ROLE_DIRECTOR = 9999L;
    public static final User TEST_ROLE_DIRECTOR = new User();

    //페이징
    public static final Pageable TEST_PAGEABLE = PageRequest.of(1, 10);

    //dto
    public static final ChangeSeatRequest TEST_CHANGE_SEAT_REQUEST = new ChangeSeatRequest(SeatEnum.VIP, TEST_SEAT.getCount() + 10, TEST_SEAT.getPrice() + 10);
    public static final int TEST_TOTAL_SEAT = TEST_SHOW.getTotalSeats() + 10;
    private static final String TEST_SEAT_REMAIN_SEAT = String.valueOf(TEST_SEAT.getRemainSeatCount() -1);

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

    private void setUpThen() {
        verify(showService).getShow(TEST_SHOW_ID);
        verify(seatRepository).findByIdAndUserId(TEST_SEAT_ID, TEST_ROLE_DIRECTOR_ID);
    }

    private void setUpGiven() {
        given(showService.getShow(TEST_SHOW_ID)).willReturn(TEST_SHOW);
        given(seatRepository.findByIdAndUserId(TEST_SEAT_ID, TEST_ROLE_DIRECTOR_ID)).willReturn(Optional.of(TEST_SEAT));
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

    @Test
    void showId에_해당하는_공연이_존재하지_않으면_InvalidRequestException예외를_던진다() {
        //given
        given(showService.getShow(anyLong())).willReturn(null);

        //when & then
        assertThrows(InvalidRequestException.class,
                () -> seatService.updateSeat(TEST_ROLE_DIRECTOR_ID, TEST_INVALID_SHOW_ID, TEST_SEAT_ID, TEST_CHANGE_SEAT_REQUEST),
                "해당 공연을 찾을 수 없습니다.");
    }

    @Test
    void seatId에_해당하는_좌석이_존재하지_않으면_InvalidRequestException예외를_던진다() {
        //given
        given(seatRepository.findByIdAndUserId(anyLong(), anyLong())).willReturn(Optional.empty());

        //when & then
        assertThrows(InvalidRequestException.class,
                () -> seatService.updateSeat(TEST_ROLE_DIRECTOR_ID, TEST_SHOW_ID, TEST_INVALID_SEAT_ID, TEST_CHANGE_SEAT_REQUEST),
                "잘못된 정보입니다.");
    }

    @Test
    void 입력된_정보에_해당하는_값이_있는_경우() {
        //given
        setUpGiven();
        doNothing().when(redisService).set(anyString(), anyString());

        //when
        seatService.updateSeat(TEST_ROLE_DIRECTOR_ID, TEST_SHOW_ID, TEST_SEAT_ID, TEST_CHANGE_SEAT_REQUEST);

        //then
        setUpThen();
        verify(redisService).set(anyString(), anyString());
    }

    @Test
    void 입력된_정보에_해당하는_값이_없는_경우_InvalidRequestException예외를_던진다() {
        //given
        given(showService.getShow(anyLong())).willReturn(null);
        given(seatRepository.findByIdAndUserId(anyLong(), anyLong())).willReturn(Optional.empty());

        //when & then
        assertThat(showService.getShow(TEST_INVALID_SHOW_ID)).isNull();
        assertThat(seatRepository.findByIdAndUserId(TEST_INVALID_SEAT_ID, TEST_INVALID_ROLE_DIRECTOR)).isEmpty();
        assertThrows(InvalidRequestException.class,
                () -> seatService.updateSeat(TEST_ROLE_DIRECTOR_ID, TEST_INVALID_SHOW_ID, TEST_INVALID_SEAT_ID, TEST_CHANGE_SEAT_REQUEST),
                "잘못된 정보입니다.");
    }

    @Test
    void 좌석_정보_업데이트_성공() {
        //given
        setUpGiven();

        //when
        seatService.updateSeat(TEST_ROLE_DIRECTOR_ID, TEST_SHOW_ID, TEST_SEAT_ID, TEST_CHANGE_SEAT_REQUEST);

        //then
        setUpThen();

        assertThat(TEST_SEAT.getName()).isEqualTo(TEST_CHANGE_SEAT_REQUEST.getName());
        assertThat(TEST_SEAT.getCount()).isEqualTo(TEST_CHANGE_SEAT_REQUEST.getCount());
        assertThat(TEST_SEAT.getPrice()).isEqualTo(TEST_CHANGE_SEAT_REQUEST.getPrice());
    }

    @Test
    void Show의_총_좌석_수_변경_성공() {
        //given
        setUpGiven();
        given(seatRepository.sumSeatCountByShowId(TEST_SHOW_ID)).willReturn(TEST_TOTAL_SEAT);

        //when
        seatService.updateSeat(TEST_ROLE_DIRECTOR_ID, TEST_SHOW_ID, TEST_SEAT_ID, TEST_CHANGE_SEAT_REQUEST);

        //then
        setUpThen();
        assertThat(TEST_SHOW.getTotalSeats()).isEqualTo(TEST_TOTAL_SEAT);
    }


    @Test
    void Seat_정보_조회_성공 () {
        //given
        given(showService.getShow(anyLong())).willReturn(TEST_SHOW);
        given(seatRepository.findByIdAndShowId(anyLong(), anyLong())).willReturn(Optional.of(TEST_SEAT));

        //when
        seatService.updateSeatCountWithLock(TEST_SHOW_ID, TEST_SEAT_ID);

        //then
        assertThat(seatRepository.findByIdAndShowId(anyLong(),anyLong())).isNotNull();
    }

    @Test
    void Seat_정보가_존재하지_않아_InvalidRequestException예외를_던진다() {
        //given
        given(showService.getShow(anyLong())).willReturn(TEST_SHOW);
        given(seatRepository.findByIdAndShowId(anyLong(), anyLong())).willReturn(Optional.empty());

        //when & then
        assertThrows(InvalidRequestException.class,
                () -> seatService.getSeat(TEST_SHOW_ID, TEST_SEAT_ID),
                "조회된 좌석 정보가 없습니다.");
    }

    @Test
    void Redis의_값이_NULL인_경우_updateSeatCountWithLock_RemainSeat값_Redis에_저장_성공 () {
        //given
        given(showService.getShow(anyLong())).willReturn(TEST_SHOW);
        given(seatRepository.findByIdAndShowId(anyLong(), anyLong())).willReturn(Optional.of(TEST_SEAT));
        given(seatRepository.remainSeatCount(TEST_SEAT_ID)).willReturn(TEST_SEAT.getRemainSeatCount());
        given(redisService.get(anyString())).willReturn(null);

        //when
        seatService.updateSeatCountWithLock(TEST_SHOW_ID, TEST_SEAT_ID);

        //then
        assertThat(seatRepository.remainSeatCount(TEST_SEAT_ID)).isNotNull();
        verify(redisService).set(anyString(), anyString());
    }

    @Test
    void Redis의_Value가_NULL이_아닌_경우_값_Seat_업데이트_성공 () {
        given(showService.getShow(anyLong())).willReturn(TEST_SHOW);
        given(seatRepository.findByIdAndShowId(anyLong(), anyLong())).willReturn(Optional.of(TEST_SEAT));
        given(seatRepository.remainSeatCount(TEST_SEAT_ID)).willReturn(TEST_SEAT.getRemainSeatCount());
        given(redisService.get(anyString())).willReturn(TEST_SEAT_REMAIN_SEAT);
        int remainSeatCount = TEST_SEAT.getRemainSeatCount();


        //when
        seatService.updateSeatCountWithLock(TEST_SHOW_ID, TEST_SEAT_ID);

        //then
        assertThat(seatRepository.remainSeatCount(TEST_SEAT_ID)).isNotNull();
        assertThat(remainSeatCount).isNotEqualTo(TEST_SEAT.getRemainSeatCount());
    }


    @Test
    void 전체_좌석_정보_조회_성공() {
        //given
        List<Seat> seats = new ArrayList<>();
        seats.add(TEST_SEAT);

        int size = seats.size();
        Long id = seats.get(0).getId();

        given(seatRepository.findAllByShowStatus()).willReturn(seats);

        //when
        List<Seat> seatList = seatService.getSeatList();

        //then
        assertThat(seatRepository.findAllByShowStatus()).isNotNull();
        assertThat(size).isEqualTo(seatList.size());
        assertThat(id).isEqualTo(seatList.get(0).getId());
    }

    @Test
    void 전체_좌석_정보_조회시_빈_배열_응답() {
        //given
        List<Seat> seats = new ArrayList<>();
        given(seatRepository.findAllByShowStatus()).willReturn(seats);
        int size = seats.size();

        //when
        List<Seat> seatList = seatService.getSeatList();

        //then
        assertThat(size).isEqualTo(seatList.size());
    }

    @Test
    void 남은_좌석_수_조회_성공 () {
        //given
        given(seatRepository.remainSeatCount(TEST_SEAT_ID)).willReturn(TEST_SEAT.getRemainSeatCount());

        //when
        int remainSeatCount = seatService.countRemainSeats(TEST_SEAT_ID);

        //then
        assertThat(seatRepository.remainSeatCount(TEST_SEAT_ID)).isEqualTo(remainSeatCount);
    }

    @Test
    void PessimisticLock적용시_좌석_조회_성공 () {
        //given
        given(seatRepository.findByIdAndShowIdWithPessimisticLock(TEST_SEAT_ID, TEST_SHOW_ID)).willReturn(Optional.of(TEST_SEAT));

        //when
        Seat byIdAndShowIdWithPessimisticLock = seatService.findByIdAndShowIdWithPessimisticLock(TEST_SHOW_ID, TEST_SEAT_ID);

        //then
        assertThat(byIdAndShowIdWithPessimisticLock).isNotNull();
        assertThat(byIdAndShowIdWithPessimisticLock).isEqualTo(TEST_SEAT);
    }

    @Test
    void PessimisticLock적용_후_좌석_조회_시_존재하지_않은_좌석_정보로_InvalidRequestException예외를_던진다 () {
        //given
        given(seatRepository.findByIdAndShowIdWithPessimisticLock(anyLong(), anyLong())).willReturn(null);

        //when
        assertThrows(NullPointerException.class,
                ()-> seatService.findByIdAndShowIdWithPessimisticLock(anyLong(), anyLong()),
                "조회된 좌석 정보가 없습니다");
    }

}