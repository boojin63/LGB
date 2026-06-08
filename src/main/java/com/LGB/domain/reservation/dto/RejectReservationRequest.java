package com.LGB.domain.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectReservationRequest(
        @NotBlank @Size(max = 500) String rejectReason
) {
}
