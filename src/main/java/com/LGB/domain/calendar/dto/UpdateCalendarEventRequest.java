package com.LGB.domain.calendar.dto;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record UpdateCalendarEventRequest(
        @Size(max = 200) String title,
        @Size(max = 10000) String description,
        @Size(max = 200) String location,
        LocalDateTime startAt,
        LocalDateTime endAt,
        Boolean allDay
) {
}
