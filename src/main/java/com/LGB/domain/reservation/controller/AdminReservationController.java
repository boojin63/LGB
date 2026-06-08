package com.LGB.domain.reservation.controller;

import com.LGB.domain.reservation.dto.RejectReservationRequest;
import com.LGB.domain.reservation.dto.ReservationPageResponse;
import com.LGB.domain.reservation.dto.ReservationResponse;
import com.LGB.domain.reservation.entity.ReservationStatus;
import com.LGB.domain.reservation.service.ReservationService;
import com.LGB.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/reservations")
@RequiredArgsConstructor
public class AdminReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<ApiResponse<ReservationPageResponse>> getReservations(
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) Long resourceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                reservationService.getAdminReservations(status, resourceId, page, size)
        ));
    }

    @PatchMapping("/{reservationId}/approve")
    public ResponseEntity<ApiResponse<ReservationResponse>> approve(
            @AuthenticationPrincipal Long adminId,
            @PathVariable Long reservationId
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                reservationService.approve(adminId, reservationId)
        ));
    }

    @PatchMapping("/{reservationId}/reject")
    public ResponseEntity<ApiResponse<ReservationResponse>> reject(
            @AuthenticationPrincipal Long adminId,
            @PathVariable Long reservationId,
            @Valid @RequestBody RejectReservationRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                reservationService.reject(adminId, reservationId, request)
        ));
    }
}
