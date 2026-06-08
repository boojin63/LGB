package com.LGB.domain.calendar.dto;

import com.LGB.domain.calendar.entity.CalendarEvent;
import java.time.LocalDateTime;

public record CalendarEventListResponse(
        Long id,
        String title,
        String location,
        LocalDateTime startAt,
        LocalDateTime endAt,
        boolean allDay
) {
    public static CalendarEventListResponse from(CalendarEvent event) {
        return new CalendarEventListResponse(
                event.getId(),
                event.getTitle(),
                event.getLocation(),
                event.getStartAt(),
                event.getEndAt(),
                event.isAllDay()
        );
    }
}
