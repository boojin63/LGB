package com.LGB.domain.calendar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record CreateCalendarEventRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 10000) String description,
        @Size(max = 200) String location,
        @NotNull LocalDateTime startAt,
        @NotNull LocalDateTime endAt,
        Boolean allDay
) {
}
