package com.LGB.domain.reservation.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LGB.domain.reservation.dto.RejectReservationRequest;
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

@WebMvcTest(AdminReservationController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        ApiAuthenticationEntryPoint.class,
        ApiAccessDeniedHandler.class,
        GlobalExceptionHandler.class
})
class AdminReservationControllerTest {

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
    void adminGetsReservations() throws Exception {
        when(reservationService.getAdminReservations(null, null, 0, 10)).thenReturn(page());

        mockMvc.perform(get("/api/admin/reservations")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.page").value(0));

        verify(reservationService).getAdminReservations(null, null, 0, 10);
    }

    @Test
    void studentCannotGetAdminReservations() throws Exception {
        mockMvc.perform(get("/api/admin/reservations")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void unauthenticatedUserCannotGetAdminReservations() throws Exception {
        mockMvc.perform(get("/api/admin/reservations"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void adminReservationFiltersArePassedToService() throws Exception {
        when(reservationService.getAdminReservations(ReservationStatus.PENDING, 10L, 1, 20))
                .thenReturn(page());

        mockMvc.perform(get("/api/admin/reservations")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN)))
                        .param("status", "PENDING")
                        .param("resourceId", "10")
                        .param("page", "1")
                        .param("size", "20"))
                .andExpect(status().isOk());

        verify(reservationService).getAdminReservations(ReservationStatus.PENDING, 10L, 1, 20);
    }

    @Test
    void adminApprovesReservation() throws Exception {
        when(reservationService.approve(1L, 100L))
                .thenReturn(response(ReservationStatus.APPROVED, null));

        mockMvc.perform(patch("/api/admin/reservations/100/approve")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("APPROVED"));

        verify(reservationService).approve(1L, 100L);
    }

    @Test
    void studentCannotApproveReservation() throws Exception {
        mockMvc.perform(patch("/api/admin/reservations/100/approve")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT))))
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedUserCannotApproveReservation() throws Exception {
        mockMvc.perform(patch("/api/admin/reservations/100/approve"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void approvingMissingReservationReturnsNotFound() throws Exception {
        when(reservationService.approve(1L, 999L))
                .thenThrow(new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        mockMvc.perform(patch("/api/admin/reservations/999/approve")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value(ErrorCode.RESERVATION_NOT_FOUND.getMessage()));
    }

    @Test
    void approvingConflictingReservationReturnsConflict() throws Exception {
        when(reservationService.approve(1L, 100L))
                .thenThrow(new CustomException(ErrorCode.RESERVATION_TIME_CONFLICT));

        mockMvc.perform(patch("/api/admin/reservations/100/approve")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(ErrorCode.RESERVATION_TIME_CONFLICT.getMessage()));
    }

    @Test
    void adminRejectsReservation() throws Exception {
        RejectReservationRequest request = new RejectReservationRequest("Unavailable");
        when(reservationService.reject(1L, 100L, request))
                .thenReturn(response(ReservationStatus.REJECTED, "Unavailable"));

        mockMvc.perform(patch("/api/admin/reservations/100/reject")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("REJECTED"))
                .andExpect(jsonPath("$.data.rejectReason").value("Unavailable"));

        verify(reservationService).reject(1L, 100L, request);
    }

    @Test
    void studentCannotRejectReservation() throws Exception {
        mockMvc.perform(patch("/api/admin/reservations/100/reject")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"rejectReason":"Unavailable"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void rejectingWithoutReasonReturnsBadRequest() throws Exception {
        mockMvc.perform(patch("/api/admin/reservations/100/reject")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"rejectReason":" "}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void rejectingAlreadyDecidedReservationReturnsConflict() throws Exception {
        RejectReservationRequest request = new RejectReservationRequest("Unavailable");
        when(reservationService.reject(1L, 100L, request))
                .thenThrow(new CustomException(ErrorCode.RESERVATION_ALREADY_DECIDED));

        mockMvc.perform(patch("/api/admin/reservations/100/reject")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(ErrorCode.RESERVATION_ALREADY_DECIDED.getMessage()));
    }

    @Test
    void invalidReservationIdReturnsBadRequest() throws Exception {
        mockMvc.perform(patch("/api/admin/reservations/not-a-number/approve")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_INPUT.getMessage()));
    }

    @Test
    void invalidStatusQueryReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/admin/reservations")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN)))
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

    private ReservationPageResponse page() {
        return new ReservationPageResponse(List.of(response(ReservationStatus.PENDING, null)), 0, 10, 1, 1, true, true);
    }

    private ReservationResponse response(ReservationStatus status, String rejectReason) {
        LocalDateTime now = LocalDateTime.of(2026, 6, 5, 10, 0);
        LocalDateTime decidedAt = status == ReservationStatus.PENDING ? null : now;
        ReservationDeciderResponse decidedBy = status == ReservationStatus.PENDING
                ? null
                : new ReservationDeciderResponse(1L, "Admin");
        return new ReservationResponse(
                100L,
                new ReservationResourceResponse(10L, "Room 101", ResourceType.ROOM, "First floor"),
                new ReservationRequesterResponse(2L, "Student", "student@lgb.local"),
                START,
                END,
                status,
                "Team project",
                rejectReason,
                decidedAt,
                decidedBy,
                now,
                now
        );
    }
}
