package com.LGB.domain.poll.dto;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record UpdatePollRequest(
        @Size(max = 200) String title,
        @Size(max = 10000) String description,
        Boolean resultVisible,
        LocalDateTime endsAt
) {
}
