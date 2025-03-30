package com.example.sparta_ticketing.domain.show.service;

import com.example.sparta_ticketing.common.config.ViewCountAop;
import com.example.sparta_ticketing.common.exception.InvalidRequestException;
import com.example.sparta_ticketing.common.exception.ShowNotFoundException;
import com.example.sparta_ticketing.common.redis.RedisService;
import com.example.sparta_ticketing.common.redis.RedisViewCountService;
import com.example.sparta_ticketing.domain.auth.entity.AuthUser;
import com.example.sparta_ticketing.domain.seat.entity.Seat;
import com.example.sparta_ticketing.domain.seat.repository.SeatRepository;
import com.example.sparta_ticketing.domain.show.dto.request.CreateShowRequestDto;
import com.example.sparta_ticketing.domain.show.dto.request.CreateShowSeatsRequestDto;
import com.example.sparta_ticketing.domain.show.dto.request.UpdateShowRequestDto;
import com.example.sparta_ticketing.domain.show.dto.response.PagingShowResponse;
import com.example.sparta_ticketing.domain.show.dto.response.ShowResponseDto;
import com.example.sparta_ticketing.domain.show.entity.Show;
import com.example.sparta_ticketing.domain.show.enums.ShowStatus;
import com.example.sparta_ticketing.domain.show.repository.ShowRepository;
import com.example.sparta_ticketing.domain.user.entity.User;
import com.example.sparta_ticketing.domain.user.service.UserService;
import com.example.sparta_ticketing.domain.viewCount.entity.ViewCount;
import com.example.sparta_ticketing.domain.viewCount.repository.ViewCountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ShowService {
    private final ShowRepository showRepository;
    private final UserService userService;
    private final SeatRepository seatRepository;
    private final RedisService redisService;
    private final RedisViewCountService redisViewCountService;
    private final ViewCountRepository viewCountRepository;

    @Transactional
    public void createShow(AuthUser authUser, CreateShowRequestDto createShowRequestDto) {
        User user = userService.findById(authUser.getId())
                .orElseThrow(()-> new EntityNotFoundException("회원을 찾지 못했습니다."));

        int totalSeats = 0;
        for (CreateShowSeatsRequestDto seat: createShowRequestDto.getSeats()) {
            totalSeats += seat.getSeatCount();
        }
        if(totalSeats == 0){
            throw new InvalidRequestException("좌석의 총 개수가 0이 될 수 없습니다.");
        }
        if(createShowRequestDto.getReservationStartDate().isBefore(LocalDateTime.now())){
            throw new InvalidRequestException("예매 시작시간을 현재 시간보다 늦게 설정해주세요.");
        }

        Show show = new Show(createShowRequestDto, totalSeats, user);
        Show savedShow = showRepository.save(show);

        List<Seat> seats = createShowRequestDto.getSeats().stream()
                .map(dto
                        -> new Seat(savedShow, dto.getSeatName(), dto.getSeatCount(), dto.getSeatPrice()))
                .collect(Collectors.toList());

        seatRepository.saveAll(seats);
        redisService.setTrigger(show);
    }

    @Transactional(readOnly = true)
    public PagingShowResponse getShowList(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Show> showPage = showRepository.findByStatus(ShowStatus.NOT_DELETED, pageable);
        List<ShowResponseDto> shows = showPage.getContent()
                .stream()
                .map(ShowResponseDto::toDto)
                .toList();
        return new PagingShowResponse(
                shows,
                showPage.getNumber(),
                showPage.getSize(),
                showPage.getTotalPages(),
                showPage.getTotalElements()
        );
    }

    /**
     * 특정 공연 조회
     *
     * @param showId (조회할 공연 Id)
     * @return Show
     */
    @Transactional(readOnly = true)
    public Show getShow(Long showId) {
        return findShow(showId);
    }

    @ViewCountAop
    @Transactional(readOnly = true)
    public ShowResponseDto findByShow(Long showId, AuthUser authUser) {
        return ShowResponseDto.form(findShow(showId), redisViewCountService.getViewCount(showId));
    }


    // DB로 조회수 관리 로직
    @Transactional
    public ShowResponseDto findByShowDb(Long showId, AuthUser authUser) {
        Show show = findShow(showId);
        User user = userService.findById(authUser.getId()).orElseThrow(()-> new EntityNotFoundException("회원을 찾지 못했습니다."));
        try {
            viewCountRepository.save(new ViewCount(user, show));
        } catch (DataIntegrityViolationException e) {
            // 이미 존재하면 무시
        }
        long viewCount = viewCountRepository.countByShow(show);

        return ShowResponseDto.form(show, viewCount);
    }


    /**
     * 특정 공연 정보 수정
     *
     * @param showId (수정할 공연 Id)
     * @param requestDto (공연명, 공연 분류, 공연 상세 정보, 지역, 시작 날짜, 종료 날짜, 예약 시작 날짜, 예약 종료 날짜)
     */
    @Transactional
    public void updateShow(Long showId, UpdateShowRequestDto requestDto) {
        Show findShow = findShow(showId);

        findShow.updateShow(requestDto);
    }

    /**
     * 특정 공연 삭제
     *
     * @param showId (삭제할 공연 Id)
     */
    @Transactional
    public void deleteShow(Long showId) {
        Show findShow = findShow(showId);

        findShow.deleteShow();
    }

    @Transactional
    public void changeShowStatus(Long showId) {
        Show findShow = findShow(showId);
        findShow.expiredShow();
    }

    @Transactional(readOnly = true)
    public List<Show> findExpiredShow() {
        return showRepository.findExpiredShow(LocalDateTime.now(), ShowStatus.NOT_DELETED);
    }


    private Show findShow(Long showId) {
        return showRepository.findShowById(showId, ShowStatus.NOT_DELETED)
                .orElseThrow(() -> new ShowNotFoundException("해당 공연을 찾을 수 없습니다."));
    }


}
