package com.LGB.domain.calendar.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.LGB.global.security.RoleType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CalendarEventServiceTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 6, 10, 10, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 6, 10, 11, 0);

    @Mock
    private CalendarEventRepository calendarEventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CalendarEventService calendarEventService;

    @Test
    void createCalendarEventWithActiveAdminCreator() {
        User admin = user(1L, "Admin", true);
        CreateCalendarEventRequest request = request(START, END, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(calendarEventRepository.save(any(CalendarEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CalendarEventDetailResponse response = calendarEventService.create(1L, request);

        assertThat(response.title()).isEqualTo("Event");
        assertThat(response.createdBy().name()).isEqualTo("Admin");
        assertThat(response.allDay()).isFalse();
    }

    @Test
    void createRejectsMissingCreator() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> calendarEventService.create(99L, request(START, END, false)))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NOT_FOUND.getMessage());
    }

    @Test
    void createRejectsInactiveCreator() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, "Admin", false)));

        assertThatThrownBy(() -> calendarEventService.create(1L, request(START, END, false)))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NOT_FOUND.getMessage());
    }

    @Test
    void createRejectsEndBeforeStart() {
        assertInvalidPeriod(() -> calendarEventService.create(
                1L,
                request(START, START.minusMinutes(1), false)
        ), true);
    }

    @Test
    void createRejectsEqualStartAndEnd() {
        assertInvalidPeriod(() -> calendarEventService.create(
                1L,
                request(START, START, false)
        ), true);
    }

    @Test
    void createRejectsAllDayStartThatIsNotMidnight() {
        assertInvalidPeriod(() -> calendarEventService.create(
                1L,
                request(START, LocalDateTime.of(2026, 6, 11, 0, 0), true)
        ), true);
    }

    @Test
    void createRejectsAllDayEndThatIsNotMidnight() {
        assertInvalidPeriod(() -> calendarEventService.create(
                1L,
                request(
                        LocalDateTime.of(2026, 6, 10, 0, 0),
                        LocalDateTime.of(2026, 6, 11, 1, 0),
                        true
                )
        ), true);
    }

    @Test
    void getEventsReturnsRepositoryOverlapOrder() {
        LocalDateTime from = LocalDateTime.of(2026, 6, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 7, 1, 0, 0);
        User admin = user(1L, "Admin", true);
        CalendarEvent first = event(1L, "First", START, END, false, admin);
        CalendarEvent second = event(2L, "Second", START.plusHours(2), END.plusHours(2), false, admin);
        when(calendarEventRepository.findOverlappingEvents(from, to))
                .thenReturn(List.of(first, second));

        List<CalendarEventListResponse> response = calendarEventService.getEvents(from, to);

        assertThat(response).extracting(CalendarEventListResponse::title)
                .containsExactly("First", "Second");
        verify(calendarEventRepository).findOverlappingEvents(from, to);
    }

    @Test
    void getEventsRejectsEqualFromAndTo() {
        assertInvalidPeriod(() -> calendarEventService.getEvents(START, START), false);
    }

    @Test
    void getEventsRejectsPeriodOverThreeHundredSixtySixDays() {
        assertInvalidPeriod(() -> calendarEventService.getEvents(START, START.plusDays(367)), false);
    }

    @Test
    void getEventsAllowsExactlyThreeHundredSixtySixDays() {
        LocalDateTime to = START.plusDays(366);
        when(calendarEventRepository.findOverlappingEvents(START, to)).thenReturn(List.of());

        calendarEventService.getEvents(START, to);

        verify(calendarEventRepository).findOverlappingEvents(START, to);
    }

    @Test
    void getEventReturnsDetail() {
        CalendarEvent event = event(1L, "Event", START, END, false, user(1L, "Admin", true));
        when(calendarEventRepository.findById(1L)).thenReturn(Optional.of(event));

        CalendarEventDetailResponse response = calendarEventService.getEvent(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("Event");
    }

    @Test
    void getEventRejectsMissingEvent() {
        when(calendarEventRepository.findById(99L)).thenReturn(Optional.empty());

        assertEventNotFound(() -> calendarEventService.getEvent(99L));
    }

    @Test
    void updateCalendarEventChangesOnlyProvidedFields() {
        CalendarEvent event = event(1L, "Old", START, END, false, user(1L, "Admin", true));
        when(calendarEventRepository.findById(1L)).thenReturn(Optional.of(event));

        CalendarEventDetailResponse response = calendarEventService.update(
                1L,
                new UpdateCalendarEventRequest("New", null, "Room 2", null, END.plusHours(1), null)
        );

        assertThat(response.title()).isEqualTo("New");
        assertThat(response.location()).isEqualTo("Room 2");
        assertThat(response.startAt()).isEqualTo(START);
        assertThat(response.endAt()).isEqualTo(END.plusHours(1));
        verify(calendarEventRepository).flush();
    }

    @Test
    void updateRejectsEmptyRequest() {
        assertInvalidRequest(() -> calendarEventService.update(
                1L,
                new UpdateCalendarEventRequest(null, null, null, null, null, null)
        ));
        verify(calendarEventRepository, never()).findById(1L);
    }

    @Test
    void updateRejectsInvalidFinalPeriod() {
        CalendarEvent event = event(1L, "Event", START, END, false, user(1L, "Admin", true));
        when(calendarEventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertInvalidPeriod(() -> calendarEventService.update(
                1L,
                new UpdateCalendarEventRequest(null, null, null, END.plusHours(1), null, null)
        ), false);
    }

    @Test
    void updateRejectsMissingEvent() {
        when(calendarEventRepository.findById(99L)).thenReturn(Optional.empty());

        assertEventNotFound(() -> calendarEventService.update(
                99L,
                new UpdateCalendarEventRequest("New", null, null, null, null, null)
        ));
    }

    @Test
    void deleteCalendarEvent() {
        CalendarEvent event = event(1L, "Event", START, END, false, user(1L, "Admin", true));
        when(calendarEventRepository.findById(1L)).thenReturn(Optional.of(event));

        calendarEventService.delete(1L);

        verify(calendarEventRepository).delete(event);
    }

    @Test
    void deleteRejectsMissingEvent() {
        when(calendarEventRepository.findById(99L)).thenReturn(Optional.empty());

        assertEventNotFound(() -> calendarEventService.delete(99L));
        verify(calendarEventRepository, never()).delete(any(CalendarEvent.class));
    }

    private CreateCalendarEventRequest request(
            LocalDateTime startAt,
            LocalDateTime endAt,
            Boolean allDay
    ) {
        return new CreateCalendarEventRequest(
                "Event",
                "Description",
                "Room 1",
                startAt,
                endAt,
                allDay
        );
    }

    private User user(Long id, String name, boolean active) {
        User user = new User(name.toLowerCase() + "@lgb.local", "encoded", name, RoleType.ADMIN);
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "active", active);
        return user;
    }

    private CalendarEvent event(
            Long id,
            String title,
            LocalDateTime startAt,
            LocalDateTime endAt,
            boolean allDay,
            User creator
    ) {
        CalendarEvent event = new CalendarEvent(
                title,
                "Description",
                "Room 1",
                startAt,
                endAt,
                allDay,
                creator
        );
        ReflectionTestUtils.setField(event, "id", id);
        ReflectionTestUtils.setField(event, "createdAt", START.minusDays(1));
        ReflectionTestUtils.setField(event, "updatedAt", START.minusDays(1));
        return event;
    }

    private void assertEventNotFound(Runnable action) {
        assertThatThrownBy(action::run)
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.CALENDAR_EVENT_NOT_FOUND.getMessage());
    }

    private void assertInvalidRequest(Runnable action) {
        assertThatThrownBy(action::run)
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_CALENDAR_EVENT_REQUEST.getMessage());
    }

    private void assertInvalidPeriod(Runnable action, boolean beforeCreatorLookup) {
        if (beforeCreatorLookup) {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, "Admin", true)));
        }
        assertThatThrownBy(action::run)
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_CALENDAR_EVENT_PERIOD.getMessage());
    }
}
