package com.example.sparta_ticketing.domain.show.service;

import com.example.sparta_ticketing.common.exception.InvalidRequestException;
import com.example.sparta_ticketing.common.redis.RedisService;
import com.example.sparta_ticketing.common.redis.RedisViewCountService;
import com.example.sparta_ticketing.domain.auth.entity.AuthUser;
import com.example.sparta_ticketing.domain.seat.enums.SeatEnum;
import com.example.sparta_ticketing.domain.seat.repository.SeatRepository;
import com.example.sparta_ticketing.domain.show.dto.request.CreateShowRequestDto;
import com.example.sparta_ticketing.domain.show.dto.request.CreateShowSeatsRequestDto;
import com.example.sparta_ticketing.domain.show.dto.request.UpdateShowRequestDto;
import com.example.sparta_ticketing.domain.show.dto.response.PagingShowResponse;
import com.example.sparta_ticketing.domain.show.dto.response.ShowResponseDto;
import com.example.sparta_ticketing.domain.show.entity.Show;
import com.example.sparta_ticketing.domain.show.enums.Category;
import com.example.sparta_ticketing.domain.show.enums.Region;
import com.example.sparta_ticketing.domain.show.enums.ShowStatus;
import com.example.sparta_ticketing.domain.show.repository.ShowRepository;
import com.example.sparta_ticketing.domain.user.entity.User;
import com.example.sparta_ticketing.domain.user.enums.UserRole;
import com.example.sparta_ticketing.domain.user.service.UserService;
import com.example.sparta_ticketing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.eq;


@ExtendWith(MockitoExtension.class)
class ShowServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ShowRepository showRepository;

    @Mock
    RedisViewCountService redisViewCountService;

    @Mock
    UserService userService;

    @Mock
    SeatRepository seatRepository;

    @Mock
    RedisService redisService;

    @InjectMocks
    private ShowService showService;

    @Test
    void 공연_생성_성공() {
        // given
        Long userId = 1L;
        User user = createUser("user@test.com", userId);
        AuthUser authUser = createAuthUser(user);

        CreateShowRequestDto requestDto = new CreateShowRequestDto(
                "테스트 공연", Category.CONCERT, "설명", Region.SEOUL,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusDays(1),
                List.of(new CreateShowSeatsRequestDto(SeatEnum.VIP, 10, 10000))
        );

        given(userService.findById(userId)).willReturn(Optional.of(user));
        given(showRepository.save(any(Show.class))).willAnswer(invocation -> {
            Show show = invocation.getArgument(0);
            ReflectionTestUtils.setField(show, "id", 1L);
            return show;
        });

        // when
        showService.createShow(authUser, requestDto);

        // then
        verify(showRepository, times(1)).save(any(Show.class));
        verify(redisService, times(1)).setTrigger(any(Show.class));
    }

    @Test
    void 공연_생성_실패_좌석수_0() {
        // given
        Long userId = 1L;
        User user = createUser("user@test.com", userId);
        AuthUser authUser = createAuthUser(user);

        CreateShowRequestDto requestDto = new CreateShowRequestDto(
                "테스트 공연", Category.CONCERT, "설명", Region.SEOUL,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusMinutes(1),
                LocalDateTime.now().plusDays(1),
                List.of(new CreateShowSeatsRequestDto(SeatEnum.VIP, 0, 10000))  // 좌석 0개
        );

        given(userService.findById(userId)).willReturn(Optional.of(user));

        // when & then
        assertThrows(InvalidRequestException.class,
                () -> showService.createShow(authUser, requestDto));
    }

    @Test
    void 공연_생성_실패_예매시간이_현재보다_이름() {
        // given
        Long userId = 1L;
        User user = createUser("user@test.com", userId);
        AuthUser authUser = createAuthUser(user);

        CreateShowRequestDto requestDto = new CreateShowRequestDto(
                "테스트 공연", Category.CONCERT, "설명", Region.SEOUL,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().minusMinutes(1), //  예매 시작 시간이 과거
                LocalDateTime.now().plusDays(1),
                List.of(new CreateShowSeatsRequestDto(SeatEnum.VIP, 10, 10000))
        );

        given(userService.findById(userId)).willReturn(Optional.of(user));

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            showService.createShow(authUser, requestDto);
        });

        assertEquals("예매 시작시간을 현재 시간보다 늦게 설정해주세요.", exception.getMessage());
    }

    @Test
    void 공연_삭제_성공() {
        // given
        Long userId = 1L;
        User user = createUser("user@test.com", userId);
        Long showId = 1L;
        Show show = createShow(user, showId);

        given(showRepository.findShowById(showId, ShowStatus.NOT_DELETED)).willReturn(Optional.of(show));

        // when
        showService.deleteShow(showId);

        // then
        assertEquals(ShowStatus.DELETED, show.getStatus());
    }

    @Test
    void 공연_상태_만료_처리_성공() {
        // given
        Long userId = 1L;
        User user = createUser("user@test.com", userId);
        Long showId = 1L;
        Show show = createShow(user, showId);

        given(showRepository.findShowById(showId, ShowStatus.NOT_DELETED)).willReturn(Optional.of(show));

        // when
        showService.changeShowStatus(showId);

        // then
        assertEquals(ShowStatus.EXPIRED, show.getStatus());
    }

    @Test
    void 만료된_공연_목록_조회() {
        // given
        Long userId = 1L;
        User user = createUser("user@test.com", userId);
        Show expiredShow = createShow(user, 1L);
        expiredShow.expiredShow();

        given(showRepository.findExpiredShow(any(LocalDateTime.class), eq(ShowStatus.NOT_DELETED)))
                .willReturn(List.of(expiredShow));

        // when
        List<Show> result = showService.findExpiredShow();

        // then
        assertEquals(1, result.size());
        assertEquals(ShowStatus.EXPIRED, result.get(0).getStatus());
    }

    @Test
    void 공연_정보_전체_리스트를_조회한다() {
        // given
        Long userId = 1L;
        User user = createUser("user", userId);


        Long showId = 1L;
        Show show1 = createShow(user, showId);

        showId = showId + 1;
        Show show2 = createShow(user, showId);
        List<Show> showList = List.of(show1, show2);

        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Show> showPage = new PageImpl<>(showList, pageable, showList.size());

        given(showRepository.findByStatus(eq(ShowStatus.NOT_DELETED), eq(pageable))).willReturn(showPage);

        // when
        PagingShowResponse responsePage = showService.getShowList(page, size);

        // then
        assertNotNull(responsePage);
        assertEquals(2, responsePage.getTotalElements());
        assertEquals(50, responsePage.getShows().get(0).getTotalSeats());
        assertEquals("공연", responsePage.getShows().get(0).getTitle());
    }

    @Test
    void 특정_공연을_조회한다() {

        // given
        Long userId = 1L;
        User user = createUser("user", userId);

        Long showId = 1L;
        Show show = createShow(user, showId);

        given(showRepository.findShowById(anyLong(), eq(ShowStatus.NOT_DELETED))).willReturn(Optional.of(show));

        // when
        Show responseShow = showService.getShow(showId);

        // then
        assertNotNull(responseShow);
        assertEquals(show.getTotalSeats(), responseShow.getTotalSeats());
        assertEquals(show.getTitle(), responseShow.getTitle());
    }

    @Test
    void 조회수가_포함된_특정_공연을_조회한다() {
        // given
        Long userId = 1L;
        User user = createUser("user", userId);
        AuthUser authUser = createAuthUser(user);

        Long showId = 1L;
        Show show = createShow(user, showId);
        given(showRepository.findShowById(anyLong(), eq(ShowStatus.NOT_DELETED))).willReturn(Optional.of(show));

        long viewCount = 123L;
        given(redisViewCountService.getViewCount(1L)).willReturn(viewCount);

        // when
        ShowResponseDto response = showService.findByShow(showId, authUser);

        // then
        assertNotNull(response);
        assertEquals(show.getTitle(), response.getTitle());
        assertEquals(viewCount, response.getViewCount());
    }

    @Test
    void 특정_공연_정보를_수정한다() {
        // given
        Long userId = 1L;
        User user = createUser("user", userId);
        AuthUser authUser = createAuthUser(user);

        Long showId = 1L;
        Show show = createShow(user, showId);

        UpdateShowRequestDto updateShowRequestDto = new UpdateShowRequestDto("공연수정", Category.MUSICAL, "내용수정", Region.SEOUL,
                LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        given(showRepository.findShowById(anyLong(), eq(ShowStatus.NOT_DELETED))).willReturn(Optional.of(show));

        //when
        showService.updateShow(showId, updateShowRequestDto);

        // then
        assertEquals("공연수정", show.getTitle());
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

    private AuthUser createAuthUser(User user) {
        return new AuthUser(user.getId(), user.getEmail(), user.getUserRole());
    }

}