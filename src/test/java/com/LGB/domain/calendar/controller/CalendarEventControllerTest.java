package com.LGB.domain.calendar.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LGB.domain.calendar.dto.CalendarEventCreatorResponse;
import com.LGB.domain.calendar.dto.CalendarEventDetailResponse;
import com.LGB.domain.calendar.dto.CalendarEventListResponse;
import com.LGB.domain.calendar.dto.CreateCalendarEventRequest;
import com.LGB.domain.calendar.dto.UpdateCalendarEventRequest;
import com.LGB.domain.calendar.service.CalendarEventService;
import com.LGB.global.exception.CustomException;
import com.LGB.global.exception.ErrorCode;
import com.LGB.global.exception.GlobalExceptionHandler;
import com.LGB.global.security.ApiAccessDeniedHandler;
import com.LGB.global.security.ApiAuthenticationEntryPoint;
import com.LGB.global.security.JwtAuthenticationFilter;
import com.LGB.global.security.JwtTokenProvider;
import com.LGB.global.security.RoleType;
import com.LGB.global.security.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CalendarEventController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        ApiAuthenticationEntryPoint.class,
        ApiAccessDeniedHandler.class,
        GlobalExceptionHandler.class
})
class CalendarEventControllerTest {

    private static final LocalDateTime FROM = LocalDateTime.of(2026, 6, 1, 0, 0);
    private static final LocalDateTime TO = LocalDateTime.of(2026, 7, 1, 0, 0);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CalendarEventService calendarEventService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void adminCreatesEvent() throws Exception {
        CreateCalendarEventRequest request = createRequest();
        when(calendarEventService.create(1L, request)).thenReturn(detailResponse());

        mockMvc.perform(post("/api/calendar/events")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Event"));

        verify(calendarEventService).create(1L, request);
    }

    @Test
    void studentCannotCreateEvent() throws Exception {
        mockMvc.perform(post("/api/calendar/events")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest())))
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedUserCannotGetEvents() throws Exception {
        mockMvc.perform(get("/api/calendar/events")
                        .param("from", FROM.toString())
                        .param("to", TO.toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void studentGetsEvents() throws Exception {
        when(calendarEventService.getEvents(FROM, TO)).thenReturn(List.of(listResponse()));

        mockMvc.perform(get("/api/calendar/events")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT)))
                        .param("from", FROM.toString())
                        .param("to", TO.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("Event"));
    }

    @Test
    void adminGetsEvents() throws Exception {
        when(calendarEventService.getEvents(FROM, TO)).thenReturn(List.of());

        mockMvc.perform(get("/api/calendar/events")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN)))
                        .param("from", FROM.toString())
                        .param("to", TO.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void invalidDateReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/calendar/events")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT)))
                        .param("from", "bad-date")
                        .param("to", TO.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_INPUT.getMessage()));
    }

    @Test
    void missingDateParameterReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/calendar/events")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT)))
                        .param("from", FROM.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_INPUT.getMessage()));
    }

    @Test
    void studentGetsEventDetail() throws Exception {
        when(calendarEventService.getEvent(1L)).thenReturn(detailResponse());

        mockMvc.perform(get("/api/calendar/events/1")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void missingEventReturnsNotFound() throws Exception {
        when(calendarEventService.getEvent(99L))
                .thenThrow(new CustomException(ErrorCode.CALENDAR_EVENT_NOT_FOUND));

        mockMvc.perform(get("/api/calendar/events/99")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ErrorCode.CALENDAR_EVENT_NOT_FOUND.getMessage()));
    }

    @Test
    void invalidEventIdReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/calendar/events/not-a-number")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_INPUT.getMessage()));
    }

    @Test
    void adminUpdatesEvent() throws Exception {
        UpdateCalendarEventRequest request =
                new UpdateCalendarEventRequest("Updated", null, null, null, null, null);
        when(calendarEventService.update(1L, request)).thenReturn(detailResponse());

        mockMvc.perform(patch("/api/calendar/events/1")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void studentCannotUpdateEvent() throws Exception {
        mockMvc.perform(patch("/api/calendar/events/1")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Updated"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void invalidCreateRequestReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/calendar/events")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":" ","startAt":"2026-06-10T10:00:00","endAt":"2026-06-10T11:00:00"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void adminDeletesEvent() throws Exception {
        doNothing().when(calendarEventService).delete(1L);

        mockMvc.perform(delete("/api/calendar/events/1")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void studentCannotDeleteEvent() throws Exception {
        mockMvc.perform(delete("/api/calendar/events/1")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT))))
                .andExpect(status().isForbidden());
    }

    private Authentication userAuthentication(Long userId, RoleType role) {
        return new UsernamePasswordAuthenticationToken(
                userId,
                null,
                List.of(new SimpleGrantedAuthority(role.asAuthority()))
        );
    }

    private CreateCalendarEventRequest createRequest() {
        return new CreateCalendarEventRequest(
                "Event",
                "Description",
                "Room 1",
                LocalDateTime.of(2026, 6, 10, 10, 0),
                LocalDateTime.of(2026, 6, 10, 11, 0),
                false
        );
    }

    private CalendarEventListResponse listResponse() {
        return new CalendarEventListResponse(
                1L,
                "Event",
                "Room 1",
                LocalDateTime.of(2026, 6, 10, 10, 0),
                LocalDateTime.of(2026, 6, 10, 11, 0),
                false
        );
    }

    private CalendarEventDetailResponse detailResponse() {
        LocalDateTime now = LocalDateTime.of(2026, 6, 5, 10, 0);
        return new CalendarEventDetailResponse(
                1L,
                "Event",
                "Description",
                "Room 1",
                LocalDateTime.of(2026, 6, 10, 10, 0),
                LocalDateTime.of(2026, 6, 10, 11, 0),
                false,
                new CalendarEventCreatorResponse(1L, "Admin"),
                now,
                now
        );
    }
}
