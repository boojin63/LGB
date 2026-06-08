package com.LGB.domain.reservation.dto;

import com.LGB.domain.user.entity.User;

public record ReservationRequesterResponse(
        Long id,
        String name,
        String email
) {
    public static ReservationRequesterResponse from(User requester) {
        return new ReservationRequesterResponse(
                requester.getId(),
                requester.getName(),
                requester.getEmail()
        );
    }
}
