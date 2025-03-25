package com.example.sparta_ticketing.domain.show.service;

import com.example.sparta_ticketing.domain.show.dto.request.UpdateShowRequestDto;
import com.example.sparta_ticketing.domain.show.entity.Show;
import com.example.sparta_ticketing.domain.show.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShowService {

    private final ShowRepository showRepository;

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

    private Show findShow(Long showId) {
        return showRepository.findById(showId).orElseThrow(() -> new IllegalArgumentException("해당 공연을 찾을 수 없습니다."));
    }
}
