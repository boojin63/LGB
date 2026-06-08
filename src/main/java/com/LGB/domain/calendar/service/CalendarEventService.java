package com.LGB.domain.calendar.service;

import com.LGB.domain.calendar.dto.CalendarEventDetailResponse;
import com.LGB.domain.calendar.dto.CalendarEventListResponse;
import com.LGB.domain.calendar.dto.CreateCalendarEventRequest;
import com.LGB.domain.calendar.dto.UpdateCalendarEventRequest;
import com.LGB.domain.calendar.entity.CalendarEvent;
import com.LGB.domain.calendar.repository.CalendarEventRepository;
import com.LGB.domain.user.entity.User;
import com.LGB.domain.user.repository.UserRepository;
import com.LGB.global.exception.CustomException;
import com.LGB.global.exception.ErrorCode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarEventService {

    private static final Duration MAX_QUERY_PERIOD = Duration.ofDays(366);

    private final CalendarEventRepository calendarEventRepository;
    private final UserRepository userRepository;

    @Transactional
    public CalendarEventDetailResponse create(Long creatorId, CreateCalendarEventRequest request) {
        User creator = userRepository.findById(creatorId)
                .filter(User::isActive)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        boolean allDay = Boolean.TRUE.equals(request.allDay());
        validateEventPeriod(request.startAt(), request.endAt(), allDay);

        CalendarEvent event = new CalendarEvent(
                request.title(),
                request.description(),
                request.location(),
                request.startAt(),
                request.endAt(),
                allDay,
                creator
        );

        return CalendarEventDetailResponse.from(calendarEventRepository.save(event));
    }

    public List<CalendarEventListResponse> getEvents(LocalDateTime from, LocalDateTime to) {
        validateQueryPeriod(from, to);

        return calendarEventRepository.findOverlappingEvents(from, to)
                .stream()
                .map(CalendarEventListResponse::from)
                .toList();
    }

    public CalendarEventDetailResponse getEvent(Long eventId) {
        return CalendarEventDetailResponse.from(findEvent(eventId));
    }

    @Transactional
    public CalendarEventDetailResponse update(Long eventId, UpdateCalendarEventRequest request) {
        validateUpdateRequest(request);

        CalendarEvent event = findEvent(eventId);
        LocalDateTime finalStartAt = request.startAt() == null ? event.getStartAt() : request.startAt();
        LocalDateTime finalEndAt = request.endAt() == null ? event.getEndAt() : request.endAt();
        boolean finalAllDay = request.allDay() == null ? event.isAllDay() : request.allDay();

        validateEventPeriod(finalStartAt, finalEndAt, finalAllDay);
        event.update(
                request.title(),
                request.description(),
                request.location(),
                request.startAt(),
                request.endAt(),
                request.allDay()
        );
        calendarEventRepository.flush();

        return CalendarEventDetailResponse.from(event);
    }

    @Transactional
    public void delete(Long eventId) {
        CalendarEvent event = findEvent(eventId);
        calendarEventRepository.delete(event);
    }

    private CalendarEvent findEvent(Long eventId) {
        return calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(ErrorCode.CALENDAR_EVENT_NOT_FOUND));
    }

    private void validateEventPeriod(LocalDateTime startAt, LocalDateTime endAt, boolean allDay) {
        if (startAt == null || endAt == null || !endAt.isAfter(startAt)) {
            throw new CustomException(ErrorCode.INVALID_CALENDAR_EVENT_PERIOD);
        }
        if (allDay
                && (!startAt.toLocalTime().equals(LocalTime.MIDNIGHT)
                || !endAt.toLocalTime().equals(LocalTime.MIDNIGHT))) {
            throw new CustomException(ErrorCode.INVALID_CALENDAR_EVENT_PERIOD);
        }
    }

    private void validateQueryPeriod(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null || !to.isAfter(from)) {
            throw new CustomException(ErrorCode.INVALID_CALENDAR_EVENT_PERIOD);
        }
        if (Duration.between(from, to).compareTo(MAX_QUERY_PERIOD) > 0) {
            throw new CustomException(ErrorCode.INVALID_CALENDAR_EVENT_PERIOD);
        }
    }

    private void validateUpdateRequest(UpdateCalendarEventRequest request) {
        if (request.title() == null
                && request.description() == null
                && request.location() == null
                && request.startAt() == null
                && request.endAt() == null
                && request.allDay() == null) {
            throw new CustomException(ErrorCode.INVALID_CALENDAR_EVENT_REQUEST);
        }
        if (request.title() != null && request.title().isBlank()) {
            throw new CustomException(ErrorCode.INVALID_CALENDAR_EVENT_REQUEST);
        }
    }
}
