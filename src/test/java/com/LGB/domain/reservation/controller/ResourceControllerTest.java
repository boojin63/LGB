package com.LGB.domain.reservation.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LGB.domain.reservation.dto.CreateResourceRequest;
import com.LGB.domain.reservation.dto.ResourceResponse;
import com.LGB.domain.reservation.dto.UpdateResourceActiveRequest;
import com.LGB.domain.reservation.dto.UpdateResourceRequest;
import com.LGB.domain.reservation.entity.ResourceType;
import com.LGB.domain.reservation.service.ResourceService;
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

@WebMvcTest(ResourceController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        ApiAuthenticationEntryPoint.class,
        ApiAccessDeniedHandler.class,
        GlobalExceptionHandler.class
})
class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ResourceService resourceService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void adminCreatesResource() throws Exception {
        CreateResourceRequest request = new CreateResourceRequest(
                "Room 101",
                ResourceType.ROOM,
                "Lecture room",
                "First floor"
        );
        when(resourceService.create(request)).thenReturn(response(true));

        mockMvc.perform(post("/api/resources")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Room 101"));

        verify(resourceService).create(request);
    }

    @Test
    void studentCannotCreateResource() throws Exception {
        mockMvc.perform(post("/api/resources")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Room 101","type":"ROOM"}
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void unauthenticatedUserCannotGetResources() throws Exception {
        mockMvc.perform(get("/api/resources"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void studentGetsResources() throws Exception {
        when(resourceService.getResources(null, false)).thenReturn(List.of(response(true)));

        mockMvc.perform(get("/api/resources")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].active").value(true));

        verify(resourceService).getResources(null, false);
    }

    @Test
    void adminGetsResources() throws Exception {
        when(resourceService.getResources(null, false)).thenReturn(List.of());

        mockMvc.perform(get("/api/resources")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN))))
                .andExpect(status().isOk());

        verify(resourceService).getResources(null, false);
    }

    @Test
    void resourceTypeFilterIsPassedToService() throws Exception {
        when(resourceService.getResources(ResourceType.ROOM, false)).thenReturn(List.of());

        mockMvc.perform(get("/api/resources")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT)))
                        .param("type", "ROOM"))
                .andExpect(status().isOk());

        verify(resourceService).getResources(ResourceType.ROOM, false);
    }

    @Test
    void studentCannotIncludeInactiveResources() throws Exception {
        when(resourceService.getResources(null, false)).thenReturn(List.of(response(true)));

        mockMvc.perform(get("/api/resources")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT)))
                        .param("includeInactive", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].active").value(true));

        verify(resourceService).getResources(null, false);
    }

    @Test
    void adminCanIncludeInactiveResources() throws Exception {
        when(resourceService.getResources(null, true))
                .thenReturn(List.of(response(true), response(false)));

        mockMvc.perform(get("/api/resources")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN)))
                        .param("includeInactive", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[1].active").value(false));

        verify(resourceService).getResources(null, true);
    }

    @Test
    void authenticatedUserGetsResourceDetail() throws Exception {
        when(resourceService.getResource(1L)).thenReturn(response(true));

        mockMvc.perform(get("/api/resources/1")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void missingResourceReturnsNotFound() throws Exception {
        when(resourceService.getResource(99L))
                .thenThrow(new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        mockMvc.perform(get("/api/resources/99")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ErrorCode.RESOURCE_NOT_FOUND.getMessage()));
    }

    @Test
    void invalidResourceIdReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/resources/not-a-number")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_INPUT.getMessage()));
    }

    @Test
    void invalidResourceTypeReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/resources")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT)))
                        .param("type", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_INPUT.getMessage()));
    }

    @Test
    void adminUpdatesResource() throws Exception {
        UpdateResourceRequest request = new UpdateResourceRequest("Updated room", null, null, null);
        when(resourceService.update(1L, request)).thenReturn(response(true));

        mockMvc.perform(patch("/api/resources/1")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(resourceService).update(1L, request);
    }

    @Test
    void studentCannotUpdateResource() throws Exception {
        mockMvc.perform(patch("/api/resources/1")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Updated room"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void invalidCreateRequestReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/resources")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":" ","type":null}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void adminUpdatesResourceActiveState() throws Exception {
        UpdateResourceActiveRequest request = new UpdateResourceActiveRequest(false);
        when(resourceService.updateActive(1L, request)).thenReturn(response(false));

        mockMvc.perform(patch("/api/resources/1/active")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.active").value(false));

        verify(resourceService).updateActive(1L, request);
    }

    @Test
    void studentCannotUpdateResourceActiveState() throws Exception {
        mockMvc.perform(patch("/api/resources/1/active")
                        .with(authentication(userAuthentication(2L, RoleType.STUDENT)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"active":false}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void missingActiveValueReturnsBadRequest() throws Exception {
        mockMvc.perform(patch("/api/resources/1/active")
                        .with(authentication(userAuthentication(1L, RoleType.ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    private Authentication userAuthentication(Long userId, RoleType role) {
        return new UsernamePasswordAuthenticationToken(
                userId,
                null,
                List.of(new SimpleGrantedAuthority(role.asAuthority()))
        );
    }

    private ResourceResponse response(boolean active) {
        LocalDateTime now = LocalDateTime.of(2026, 6, 5, 10, 0);
        return new ResourceResponse(
                1L,
                "Room 101",
                ResourceType.ROOM,
                "Lecture room",
                "First floor",
                active,
                now,
                now
        );
    }
}
