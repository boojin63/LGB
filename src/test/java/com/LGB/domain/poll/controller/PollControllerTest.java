package com.LGB.domain.poll.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LGB.domain.poll.dto.CreatePollRequest;
import com.LGB.domain.poll.dto.PollDetailResponse;
import com.LGB.domain.poll.dto.PollOptionResponse;
import com.LGB.domain.poll.dto.PollPageResponse;
import com.LGB.domain.poll.dto.PollResultOptionResponse;
import com.LGB.domain.poll.dto.PollResultResponse;
import com.LGB.domain.poll.dto.PollVoteResponse;
import com.LGB.domain.poll.dto.UpdatePollRequest;
import com.LGB.domain.poll.dto.VotePollRequest;
import com.LGB.domain.poll.entity.PollStatus;
import com.LGB.domain.poll.service.PollService;
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

@WebMvcTest(PollController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        ApiAuthenticationEntryPoint.class,
        ApiAccessDeniedHandler.class,
        GlobalExceptionHandler.class
})
class PollControllerTest {

    private static final LocalDateTime STARTS_AT = LocalDateTime.of(2026, 6, 10, 10, 0);
    private static final LocalDateTime ENDS_AT = LocalDateTime.of(2026, 6, 20, 18, 0);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PollService pollService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void adminCreatesPoll() throws Exception {
        CreatePollRequest request = createRequest();
        when(pollService.create(1L, request)).thenReturn(detailResponse(PollStatus.OPEN));

        mockMvc.perform(post("/api/polls")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Poll"));

        verify(pollService).create(1L, request);
    }

    @Test
    void studentCannotCreatePoll() throws Exception {
        mockMvc.perform(post("/api/polls")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void unauthenticatedUserCannotGetPolls() throws Exception {
        mockMvc.perform(get("/api/polls"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void authenticatedUserGetsPolls() throws Exception {
        when(pollService.getPolls(null, 0, 10, 2L)).thenReturn(pageResponse());

        mockMvc.perform(get("/api/polls")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.page").value(0));

        verify(pollService).getPolls(null, 0, 10, 2L);
    }

    @Test
    void statusQueryParameterIsPassedToService() throws Exception {
        when(pollService.getPolls(PollStatus.OPEN, 1, 20, 2L)).thenReturn(pageResponse());

        mockMvc.perform(get("/api/polls")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT)))
                        .param("status", "OPEN")
                        .param("page", "1")
                        .param("size", "20"))
                .andExpect(status().isOk());

        verify(pollService).getPolls(PollStatus.OPEN, 1, 20, 2L);
    }

    @Test
    void invalidStatusQueryReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/polls")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT)))
                        .param("status", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_INPUT.getMessage()));
    }

    @Test
    void authenticatedUserGetsPollDetail() throws Exception {
        when(pollService.getPoll(100L, 2L)).thenReturn(detailResponse(PollStatus.OPEN));

        mockMvc.perform(get("/api/polls/100")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(100));

        verify(pollService).getPoll(100L, 2L);
    }

    @Test
    void missingPollDetailReturnsNotFound() throws Exception {
        when(pollService.getPoll(999L, 2L)).thenThrow(new CustomException(ErrorCode.POLL_NOT_FOUND));

        mockMvc.perform(get("/api/polls/999")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ErrorCode.POLL_NOT_FOUND.getMessage()));
    }

    @Test
    void invalidPollIdReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/polls/not-a-number")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_INPUT.getMessage()));
    }

    @Test
    void adminUpdatesPoll() throws Exception {
        UpdatePollRequest request = new UpdatePollRequest("Updated", null, true, null);
        when(pollService.update(100L, request)).thenReturn(detailResponse(PollStatus.OPEN));

        mockMvc.perform(patch("/api/polls/100")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(pollService).update(100L, request);
    }

    @Test
    void studentCannotUpdatePoll() throws Exception {
        mockMvc.perform(patch("/api/polls/100")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Updated"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void invalidCreateRequestReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/polls")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":" ","startsAt":"2026-06-10T10:00:00","endsAt":"2026-06-20T18:00:00","options":["A","B"]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void adminClosesPoll() throws Exception {
        when(pollService.close(100L)).thenReturn(detailResponse(PollStatus.CLOSED));

        mockMvc.perform(patch("/api/polls/100/close")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CLOSED"));

        verify(pollService).close(100L);
    }

    @Test
    void studentCannotClosePoll() throws Exception {
        mockMvc.perform(patch("/api/polls/100/close")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT))))
                .andExpect(status().isForbidden());
    }

    @Test
    void studentVotesPoll() throws Exception {
        VotePollRequest request = new VotePollRequest(10L);
        when(pollService.vote(2L, 100L, request))
                .thenReturn(new PollVoteResponse(100L, 10L, LocalDateTime.of(2026, 6, 10, 11, 0)));

        mockMvc.perform(post("/api/polls/100/vote")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.pollId").value(100))
                .andExpect(jsonPath("$.data.optionId").value(10));

        verify(pollService).vote(2L, 100L, request);
    }

    @Test
    void adminCannotVotePoll() throws Exception {
        mockMvc.perform(post("/api/polls/100/vote")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"optionId":10}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedUserCannotVotePoll() throws Exception {
        mockMvc.perform(post("/api/polls/100/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"optionId":10}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidVoteRequestReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/polls/100/vote")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void duplicateVoteReturnsConflict() throws Exception {
        VotePollRequest request = new VotePollRequest(10L);
        when(pollService.vote(2L, 100L, request))
                .thenThrow(new CustomException(ErrorCode.POLL_ALREADY_VOTED));

        mockMvc.perform(post("/api/polls/100/vote")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(ErrorCode.POLL_ALREADY_VOTED.getMessage()));
    }

    @Test
    void adminGetsResult() throws Exception {
        when(pollService.getResult(1L, RoleType.ADMIN, 100L)).thenReturn(resultResponse());

        mockMvc.perform(get("/api/polls/100/result")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalVotes").value(3));

        verify(pollService).getResult(1L, RoleType.ADMIN, 100L);
    }

    @Test
    void studentGetsResult() throws Exception {
        when(pollService.getResult(2L, RoleType.STUDENT, 100L)).thenReturn(resultResponse());

        mockMvc.perform(get("/api/polls/100/result")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT))))
                .andExpect(status().isOk());

        verify(pollService).getResult(2L, RoleType.STUDENT, 100L);
    }

    @Test
    void resultAccessDeniedFromServiceReturnsForbidden() throws Exception {
        when(pollService.getResult(2L, RoleType.STUDENT, 100L))
                .thenThrow(new CustomException(ErrorCode.POLL_RESULT_ACCESS_DENIED));

        mockMvc.perform(get("/api/polls/100/result")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(ErrorCode.POLL_RESULT_ACCESS_DENIED.getMessage()));
    }

    private Authentication userAuthentication(Long userId, RoleType role) {
        return new UsernamePasswordAuthenticationToken(
                userId,
                null,
                List.of(new SimpleGrantedAuthority(role.asAuthority()))
        );
    }

    private CreatePollRequest createRequest() {
        return new CreatePollRequest(
                "Poll",
                "Description",
                true,
                false,
                STARTS_AT,
                ENDS_AT,
                List.of("A", "B")
        );
    }

    private PollPageResponse pageResponse() {
        return new PollPageResponse(List.of(), 0, 10, 0, 0, true, true);
    }

    private PollDetailResponse detailResponse(PollStatus status) {
        LocalDateTime now = LocalDateTime.of(2026, 6, 5, 10, 0);
        return new PollDetailResponse(
                100L,
                "Poll",
                "Description",
                status,
                true,
                false,
                STARTS_AT,
                ENDS_AT,
                List.of(
                        new PollOptionResponse(10L, "A", 1),
                        new PollOptionResponse(11L, "B", 2)
                ),
                false,
                now,
                now
        );
    }

    private PollResultResponse resultResponse() {
        return new PollResultResponse(
                100L,
                "Poll",
                3L,
                List.of(
                        new PollResultOptionResponse(10L, "A", 1, 2L, 66.666),
                        new PollResultOptionResponse(11L, "B", 2, 1L, 33.333)
                ),
                true,
                true,
                PollStatus.OPEN,
                STARTS_AT,
                ENDS_AT
        );
    }
}
