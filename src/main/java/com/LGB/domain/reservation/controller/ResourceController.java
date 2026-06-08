package com.LGB.domain.reservation.controller;

import com.LGB.domain.reservation.dto.CreateResourceRequest;
import com.LGB.domain.reservation.dto.ResourceResponse;
import com.LGB.domain.reservation.dto.UpdateResourceActiveRequest;
import com.LGB.domain.reservation.dto.UpdateResourceRequest;
import com.LGB.domain.reservation.entity.ResourceType;
import com.LGB.domain.reservation.service.ResourceService;
import com.LGB.global.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {

    private static final String ADMIN_AUTHORITY = "ROLE_ADMIN";

    private final ResourceService resourceService;

    @PostMapping
    public ResponseEntity<ApiResponse<ResourceResponse>> create(
            @Valid @RequestBody CreateResourceRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(resourceService.create(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ResourceResponse>>> getResources(
            @RequestParam(required = false) ResourceType type,
            @RequestParam(defaultValue = "false") boolean includeInactive,
            Authentication authentication
    ) {
        boolean canIncludeInactive = includeInactive && hasAdminAuthority(authentication);
        return ResponseEntity.ok(
                ApiResponse.success(resourceService.getResources(type, canIncludeInactive))
        );
    }

    @GetMapping("/{resourceId}")
    public ResponseEntity<ApiResponse<ResourceResponse>> getResource(
            @PathVariable Long resourceId
    ) {
        return ResponseEntity.ok(ApiResponse.success(resourceService.getResource(resourceId)));
    }

    @PatchMapping("/{resourceId}")
    public ResponseEntity<ApiResponse<ResourceResponse>> update(
            @PathVariable Long resourceId,
            @Valid @RequestBody UpdateResourceRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(resourceService.update(resourceId, request)));
    }

    @PatchMapping("/{resourceId}/active")
    public ResponseEntity<ApiResponse<ResourceResponse>> updateActive(
            @PathVariable Long resourceId,
            @Valid @RequestBody UpdateResourceActiveRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(resourceService.updateActive(resourceId, request))
        );
    }

    private boolean hasAdminAuthority(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> ADMIN_AUTHORITY.equals(authority.getAuthority()));
    }
}
