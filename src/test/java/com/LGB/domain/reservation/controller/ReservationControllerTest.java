package com.LGB.domain.reservation.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LGB.domain.reservation.dto.CreateReservationRequest;
import com.LGB.domain.reservation.dto.ReservationDeciderResponse;
import com.LGB.domain.reservation.dto.ReservationPageResponse;
import com.LGB.domain.reservation.dto.ReservationRequesterResponse;
import com.LGB.domain.reservation.dto.ReservationResourceResponse;
import com.LGB.domain.reservation.dto.ReservationResponse;
import com.LGB.domain.reservation.entity.ReservationStatus;
import com.LGB.domain.reservation.entity.ResourceType;
import com.LGB.domain.reservation.service.ReservationService;
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

@WebMvcTest(ReservationController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        ApiAuthenticationEntryPoint.class,
        ApiAccessDeniedHandler.class,
        GlobalExceptionHandler.class
})
class ReservationControllerTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 6, 10, 10, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 6, 10, 12, 0);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void studentCreatesReservation() throws Exception {
        CreateReservationRequest request = createRequest();
        when(reservationService.create(2L, request)).thenReturn(response(ReservationStatus.PENDING));

        mockMvc.perform(post("/api/reservations")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PENDING"));

        verify(reservationService).create(2L, request);
    }

    @Test
    void adminCannotCreateReservation() throws Exception {
        mockMvc.perform(post("/api/reservations")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void unauthenticatedUserCannotCreateReservation() throws Exception {
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void invalidCreateRequestReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/reservations")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"resourceId":1,"startAt":"2026-06-10T10:00:00","endAt":"2026-06-10T12:00:00","purpose":" "}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void studentGetsMyReservations() throws Exception {
        when(reservationService.getMyReservations(2L, null, 0, 10)).thenReturn(page());

        mockMvc.perform(get("/api/reservations/my")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.page").value(0));

        verify(reservationService).getMyReservations(2L, null, 0, 10);
    }

    @Test
    void studentGetsMyReservationsWithStatus() throws Exception {
        when(reservationService.getMyReservations(2L, ReservationStatus.PENDING, 0, 10))
                .thenReturn(page());

        mockMvc.perform(get("/api/reservations/my")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT)))
                        .param("status", "PENDING"))
                .andExpect(status().isOk());

        verify(reservationService).getMyReservations(2L, ReservationStatus.PENDING, 0, 10);
    }

    @Test
    void adminCannotGetMyReservations() throws Exception {
        mockMvc.perform(get("/api/reservations/my")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN))))
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedUserCannotGetMyReservations() throws Exception {
        mockMvc.perform(get("/api/reservations/my"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void studentGetsOwnReservationDetail() throws Exception {
        when(reservationService.getReservation(2L, RoleType.STUDENT, 100L))
                .thenReturn(response(ReservationStatus.PENDING));

        mockMvc.perform(get("/api/reservations/100")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(100));

        verify(reservationService).getReservation(2L, RoleType.STUDENT, 100L);
    }

    @Test
    void studentAccessingOtherReservationReturnsForbiddenFromService() throws Exception {
        when(reservationService.getReservation(2L, RoleType.STUDENT, 100L))
                .thenThrow(new CustomException(ErrorCode.RESERVATION_ACCESS_DENIED));

        mockMvc.perform(get("/api/reservations/100")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message")
                        .value(ErrorCode.RESERVATION_ACCESS_DENIED.getMessage()));
    }

    @Test
    void adminGetsReservationDetail() throws Exception {
        when(reservationService.getReservation(1L, RoleType.ADMIN, 100L))
                .thenReturn(response(ReservationStatus.PENDING));

        mockMvc.perform(get("/api/reservations/100")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN))))
                .andExpect(status().isOk());

        verify(reservationService).getReservation(1L, RoleType.ADMIN, 100L);
    }

    @Test
    void missingReservationReturnsNotFound() throws Exception {
        when(reservationService.getReservation(2L, RoleType.STUDENT, 999L))
                .thenThrow(new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        mockMvc.perform(get("/api/reservations/999")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value(ErrorCode.RESERVATION_NOT_FOUND.getMessage()));
    }

    @Test
    void invalidReservationIdReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/reservations/not-a-number")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_INPUT.getMessage()));
    }

    @Test
    void invalidStatusQueryReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/reservations/my")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT)))
                        .param("status", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_INPUT.getMessage()));
    }

    private Authentication userAuthentication(Long userId, RoleType role) {
        return new UsernamePasswordAuthenticationToken(
                userId,
                null,
                List.of(new SimpleGrantedAuthority(role.asAuthority()))
        );
    }

    private CreateReservationRequest createRequest() {
        return new CreateReservationRequest(10L, START, END, "Team project");
    }

    private ReservationPageResponse page() {
        return new ReservationPageResponse(List.of(response(ReservationStatus.PENDING)), 0, 10, 1, 1, true, true);
    }

    private ReservationResponse response(ReservationStatus status) {
        LocalDateTime now = LocalDateTime.of(2026, 6, 5, 10, 0);
        return new ReservationResponse(
                100L,
                new ReservationResourceResponse(10L, "Room 101", ResourceType.ROOM, "First floor"),
                new ReservationRequesterResponse(2L, "Student", "student@lgb.local"),
                START,
                END,
                status,
                "Team project",
                null,
                null,
                (ReservationDeciderResponse) null,
                now,
                now
        );
    }
}
