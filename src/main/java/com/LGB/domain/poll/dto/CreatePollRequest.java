package com.LGB.domain.poll.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

public record CreatePollRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 10000) String description,
        Boolean anonymous,
        Boolean resultVisible,
        @NotNull LocalDateTime startsAt,
        @NotNull LocalDateTime endsAt,
        @NotNull @Size(min = 2, max = 10) List<@NotBlank @Size(max = 200) String> options
) {
}
