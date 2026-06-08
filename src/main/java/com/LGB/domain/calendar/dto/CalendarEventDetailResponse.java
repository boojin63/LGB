package com.LGB.domain.calendar.dto;

import com.LGB.domain.calendar.entity.CalendarEvent;
import java.time.LocalDateTime;

public record CalendarEventDetailResponse(
        Long id,
        String title,
        String description,
        String location,
        LocalDateTime startAt,
        LocalDateTime endAt,
        boolean allDay,
        CalendarEventCreatorResponse createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CalendarEventDetailResponse from(CalendarEvent event) {
        return new CalendarEventDetailResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getStartAt(),
                event.getEndAt(),
                event.isAllDay(),
                CalendarEventCreatorResponse.from(event.getCreatedBy()),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }
}
