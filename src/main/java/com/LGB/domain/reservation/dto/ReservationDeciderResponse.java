package com.LGB.domain.reservation.dto;

import com.LGB.domain.user.entity.User;

public record ReservationDeciderResponse(
        Long id,
        String name
) {
    public static ReservationDeciderResponse from(User decider) {
        if (decider == null) {
            return null;
        }
        return new ReservationDeciderResponse(decider.getId(), decider.getName());
    }
}
