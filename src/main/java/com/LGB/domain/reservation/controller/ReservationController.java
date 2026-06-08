package com.LGB.domain.reservation.controller;

import com.LGB.domain.reservation.dto.CreateReservationRequest;
import com.LGB.domain.reservation.dto.ReservationPageResponse;
import com.LGB.domain.reservation.dto.ReservationResponse;
import com.LGB.domain.reservation.entity.ReservationStatus;
import com.LGB.domain.reservation.service.ReservationService;
import com.LGB.global.security.RoleType;
import com.LGB.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private static final String ADMIN_AUTHORITY = "ROLE_ADMIN";

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResponse>> create(
            @AuthenticationPrincipal Long requesterId,
            @Valid @RequestBody CreateReservationRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(reservationService.create(requesterId, request)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<ReservationPageResponse>> getMyReservations(
            @AuthenticationPrincipal Long requesterId,
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                reservationService.getMyReservations(requesterId, status, page, size)
        ));
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<ReservationResponse>> getReservation(
            @AuthenticationPrincipal Long currentUserId,
            Authentication authentication,
            @PathVariable Long reservationId
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                reservationService.getReservation(
                        currentUserId,
                        extractRole(authentication),
                        reservationId
                )
        ));
    }

    private RoleType extractRole(Authentication authentication) {
        boolean admin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> ADMIN_AUTHORITY.equals(authority.getAuthority()));
        return admin ? RoleType.ADMIN : RoleType.STUDENT;
    }
}
