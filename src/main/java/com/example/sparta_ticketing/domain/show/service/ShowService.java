package com.example.sparta_ticketing.domain.show.service;

import com.example.sparta_ticketing.domain.auth.entity.AuthUser;
import com.example.sparta_ticketing.domain.show.dto.request.UpdateShowRequestDto;
import com.example.sparta_ticketing.domain.show.dto.response.ShowResponseDto;
import com.example.sparta_ticketing.domain.show.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShowService {

    private final ShowRepository showRepository;

    /**
     * 특정 공연 조회
     *
     * @param authUser (인증된 유저 정보)
     * @param showId (조회할 공연 Id)
     * @return ShowResponseDto
     */
    public ShowResponseDto getShow(AuthUser authUser, Long showId) {
        return null;
    }

    /**
     * 특정 공연 정보 수정
     *
     * @param authUser (인증된 유저 정보)
     * @param showId (수정할 공연 Id)
     * @param requestDto (공연명, 공연 분류, 공연 상세 정보, 지역, 시작 날짜, 종료 날짜, 예약 시작 날짜, 예약 종료 날짜, 총 좌석, 이미지 )
     */
    public void updateShow(AuthUser authUser, Long showId, UpdateShowRequestDto requestDto) {

    }

    /**
     * 특정 공연 삭제
     *
     * @param authUser (인증된 유저 정보)
     * @param showId (삭제할 공연 Id)
     */
    public void deleteShow(AuthUser authUser, Long showId) {

    }
}
