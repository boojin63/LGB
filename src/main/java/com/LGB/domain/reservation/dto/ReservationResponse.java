package com.LGB.domain.reservation.dto;

import com.LGB.domain.reservation.entity.Reservation;
import com.LGB.domain.reservation.entity.ReservationStatus;
import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        ReservationResourceResponse resource,
        ReservationRequesterResponse requester,
        LocalDateTime startAt,
        LocalDateTime endAt,
        ReservationStatus status,
        String purpose,
        String rejectReason,
        LocalDateTime decidedAt,
        ReservationDeciderResponse decidedBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                ReservationResourceResponse.from(reservation.getResource()),
                ReservationRequesterResponse.from(reservation.getRequester()),
                reservation.getStartAt(),
                reservation.getEndAt(),
                reservation.getStatus(),
                reservation.getPurpose(),
                reservation.getRejectReason(),
                reservation.getDecidedAt(),
                ReservationDeciderResponse.from(reservation.getDecidedBy()),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt()
        );
    }
}
