package com.LGB.domain.calendar.dto;

import com.LGB.domain.user.entity.User;

public record CalendarEventCreatorResponse(
        Long id,
        String name
) {
    public static CalendarEventCreatorResponse from(User creator) {
        return new CalendarEventCreatorResponse(creator.getId(), creator.getName());
    }
}
