package com.LGB.domain.reservation.dto;

import java.util.List;
import org.springframework.data.domain.Page;

public record ReservationPageResponse(
        List<ReservationResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
    public static ReservationPageResponse from(Page<ReservationResponse> reservations) {
        return new ReservationPageResponse(
                reservations.getContent(),
                reservations.getNumber(),
                reservations.getSize(),
                reservations.getTotalElements(),
                reservations.getTotalPages(),
                reservations.isFirst(),
                reservations.isLast()
        );
    }
}
