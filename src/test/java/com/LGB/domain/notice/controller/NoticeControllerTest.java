package com.LGB.domain.notice.controller;

import static org.mockito.ArgumentMatchers.any;
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

import com.LGB.domain.notice.dto.CreateNoticeRequest;
import com.LGB.domain.notice.dto.NoticeAuthorResponse;
import com.LGB.domain.notice.dto.NoticeDetailResponse;
import com.LGB.domain.notice.dto.NoticePageResponse;
import com.LGB.domain.notice.dto.UpdateNoticeRequest;
import com.LGB.domain.notice.service.NoticeService;
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

@WebMvcTest(NoticeController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        ApiAuthenticationEntryPoint.class,
        ApiAccessDeniedHandler.class,
        GlobalExceptionHandler.class
})
class NoticeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NoticeService noticeService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void adminCreatesNotice() throws Exception {
        CreateNoticeRequest request = new CreateNoticeRequest("Title", "Content", true);
        when(noticeService.create(1L, request)).thenReturn(detailResponse());

        mockMvc.perform(post("/api/notices")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Title"));

        verify(noticeService).create(1L, request);
    }

    @Test
    void studentCannotCreateNotice() throws Exception {
        mockMvc.perform(post("/api/notices")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Title","content":"Content","pinned":false}
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void unauthenticatedUserCannotGetNotices() throws Exception {
        mockMvc.perform(get("/api/notices"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void studentGetsNotices() throws Exception {
        when(noticeService.getNotices(0, 10)).thenReturn(emptyPage());

        mockMvc.perform(get("/api/notices")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.page").value(0));
    }

    @Test
    void adminGetsNotices() throws Exception {
        when(noticeService.getNotices(0, 10)).thenReturn(emptyPage());

        mockMvc.perform(get("/api/notices")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN))))
                .andExpect(status().isOk());
    }

    @Test
    void studentGetsNoticeDetail() throws Exception {
        when(noticeService.getNotice(1L)).thenReturn(detailResponse());

        mockMvc.perform(get("/api/notices/1")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void missingNoticeReturnsNotFound() throws Exception {
        when(noticeService.getNotice(99L))
                .thenThrow(new CustomException(ErrorCode.NOTICE_NOT_FOUND));

        mockMvc.perform(get("/api/notices/99")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ErrorCode.NOTICE_NOT_FOUND.getMessage()));
    }

    @Test
    void adminUpdatesNotice() throws Exception {
        UpdateNoticeRequest request = new UpdateNoticeRequest("Updated", null, null);
        when(noticeService.update(1L, request)).thenReturn(detailResponse());

        mockMvc.perform(patch("/api/notices/1")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void studentCannotUpdateNotice() throws Exception {
        mockMvc.perform(patch("/api/notices/1")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Updated"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminDeletesNotice() throws Exception {
        doNothing().when(noticeService).delete(1L);

        mockMvc.perform(delete("/api/notices/1")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void studentCannotDeleteNotice() throws Exception {
        mockMvc.perform(delete("/api/notices/1")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT))))
                .andExpect(status().isForbidden());
    }

    @Test
    void invalidCreateRequestReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/notices")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":" ","content":"Content"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void invalidNoticeIdReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/notices/not-a-number")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT))))
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

    private NoticeDetailResponse detailResponse() {
        LocalDateTime now = LocalDateTime.of(2026, 6, 5, 10, 0);
        return new NoticeDetailResponse(
                1L,
                "Title",
                "Content",
                new NoticeAuthorResponse(1L, "Admin"),
                true,
                0,
                now,
                now
        );
    }

    private NoticePageResponse emptyPage() {
        return new NoticePageResponse(List.of(), 0, 10, 0, 0, true, true);
    }
}
