package com.example.sparta_ticketing.domain.show.controller;

import com.example.sparta_ticketing.domain.auth.entity.AuthUser;
import com.example.sparta_ticketing.domain.show.dto.request.UpdateShowRequestDto;
import com.example.sparta_ticketing.domain.show.dto.response.ShowResponseDto;
import com.example.sparta_ticketing.domain.show.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;

    /**
     * 특정 공연 조회 API
     */
    @GetMapping("shows/{showId}")
    public ResponseEntity<ShowResponseDto> getShow (
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long showId
    ) {
        return ResponseEntity.ok(showService.getShow(authUser, showId));
    }

    /**
     * 공연 수정 API
     */
    @PatchMapping("/shows/{showId}")
    public void updateShow(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long showId,
            @RequestBody UpdateShowRequestDto updateShowRequestDto
    ) {
        showService.updateShow(authUser, showId, updateShowRequestDto);
    }

    /**
     * 공연 삭제 API
     */
    @DeleteMapping("/show/{showId}")
    public void deleteShow(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long showId
    ) {
        showService.deleteShow(authUser, showId);
    }
}
