package com.LGB.domain.reservation.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateResourceActiveRequest(
        @NotNull Boolean active
) {
}
