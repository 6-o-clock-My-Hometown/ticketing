package com.example.sparta_ticketing.domain.show.controller;

import com.example.sparta_ticketing.domain.show.dto.request.UpdateShowRequestDto;
import com.example.sparta_ticketing.domain.show.dto.response.ShowResponseDto;
import com.example.sparta_ticketing.domain.show.service.ShowService;
import com.example.sparta_ticketing.domain.user.enums.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;

    /**
     * 특정 공연 조회 API
     */
    @GetMapping("/shows/{showId}")
    public ResponseEntity<ShowResponseDto> getShow (
            @PathVariable Long showId
    ) {
        return ResponseEntity.ok(ShowResponseDto.toDto(showService.getShow(showId)));
    }

    /**
     * 공연 수정 API
     */
    @Secured(UserRole.Authority.DIRECTOR)
    @PatchMapping("/shows/{showId}")
    public void updateShow(
            @PathVariable Long showId,
            @Valid @RequestBody UpdateShowRequestDto updateShowRequestDto
    ) {
        showService.updateShow(showId, updateShowRequestDto);
    }

    /**
     * 공연 삭제 API
     */
    @Secured(UserRole.Authority.DIRECTOR)
    @DeleteMapping("/shows/{showId}")
    public void deleteShow(
            @PathVariable Long showId
    ) {
        showService.deleteShow(showId);
    }
}
