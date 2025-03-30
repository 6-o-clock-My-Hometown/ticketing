package com.example.sparta_ticketing.domain.ticket.controller;

import com.example.sparta_ticketing.domain.auth.entity.AuthUser;
import com.example.sparta_ticketing.domain.ticket.dto.request.CreateSeatReservationRequest;
import com.example.sparta_ticketing.domain.ticket.dto.response.TicketResponse;
import com.example.sparta_ticketing.domain.ticket.service.TicketService;
import com.example.sparta_ticketing.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/reservations")
@RestController
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @Secured(UserRole.Authority.USER)
    @PostMapping()
    public ResponseEntity<TicketResponse> reserveSeat (@AuthenticationPrincipal AuthUser user, @RequestBody CreateSeatReservationRequest request) {
        return ResponseEntity.ok(ticketService.reserveSeat(user.getId(), request));
    }

    @Secured(UserRole.Authority.USER)
    @PostMapping("/pessimisticLock")
    public ResponseEntity<TicketResponse> reserveSeatWithPessimisticLock (@AuthenticationPrincipal AuthUser user, @RequestBody CreateSeatReservationRequest request) {
        return ResponseEntity.ok(ticketService.reserveSeatWithPessimisticLock(user.getId(), request));
    }

    @Secured(UserRole.Authority.USER)
    @PostMapping("/withoutLock")
    public ResponseEntity<TicketResponse> reserveSeatWithoutLock (@AuthenticationPrincipal AuthUser user, @RequestBody CreateSeatReservationRequest request) {
        return ResponseEntity.ok(ticketService.reserveSeatWithoutLock(user.getId(), request));
    }

    @Secured(UserRole.Authority.USER)
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getReserve (@AuthenticationPrincipal AuthUser user, @PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getReserve(user.getId(), id));
    }

    @Secured(UserRole.Authority.USER)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelReserveSeat (@AuthenticationPrincipal AuthUser user, @PathVariable Long id) {
        ticketService.cancelReserveSeat(user.getId(), id);
        return ResponseEntity.ok().build();
    }
}
