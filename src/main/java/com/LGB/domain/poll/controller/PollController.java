package com.LGB.domain.poll.controller;

import com.LGB.domain.poll.dto.CreatePollRequest;
import com.LGB.domain.poll.dto.PollDetailResponse;
import com.LGB.domain.poll.dto.PollPageResponse;
import com.LGB.domain.poll.dto.PollResultResponse;
import com.LGB.domain.poll.dto.PollVoteResponse;
import com.LGB.domain.poll.dto.UpdatePollRequest;
import com.LGB.domain.poll.dto.VotePollRequest;
import com.LGB.domain.poll.entity.PollStatus;
import com.LGB.domain.poll.service.PollService;
import com.LGB.global.response.ApiResponse;
import com.LGB.global.security.RoleType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/polls")
@RequiredArgsConstructor
public class PollController {

    private static final String ADMIN_AUTHORITY = "ROLE_ADMIN";

    private final PollService pollService;

    @PostMapping
    public ResponseEntity<ApiResponse<PollDetailResponse>> create(
            @AuthenticationPrincipal Long adminId,
            @Valid @RequestBody CreatePollRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(pollService.create(adminId, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PollPageResponse>> getPolls(
            @AuthenticationPrincipal Long currentUserId,
            @RequestParam(required = false) PollStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                pollService.getPolls(status, page, size, currentUserId)
        ));
    }

    @GetMapping("/{pollId}")
    public ResponseEntity<ApiResponse<PollDetailResponse>> getPoll(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long pollId
    ) {
        return ResponseEntity.ok(ApiResponse.success(pollService.getPoll(pollId, currentUserId)));
    }

    @PatchMapping("/{pollId}")
    public ResponseEntity<ApiResponse<PollDetailResponse>> update(
            @PathVariable Long pollId,
            @Valid @RequestBody UpdatePollRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(pollService.update(pollId, request)));
    }

    @PatchMapping("/{pollId}/close")
    public ResponseEntity<ApiResponse<PollDetailResponse>> close(@PathVariable Long pollId) {
        return ResponseEntity.ok(ApiResponse.success(pollService.close(pollId)));
    }

    @PostMapping("/{pollId}/vote")
    public ResponseEntity<ApiResponse<PollVoteResponse>> vote(
            @AuthenticationPrincipal Long voterId,
            @PathVariable Long pollId,
            @Valid @RequestBody VotePollRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(pollService.vote(voterId, pollId, request)));
    }

    @GetMapping("/{pollId}/result")
    public ResponseEntity<ApiResponse<PollResultResponse>> getResult(
            @AuthenticationPrincipal Long currentUserId,
            Authentication authentication,
            @PathVariable Long pollId
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                pollService.getResult(currentUserId, extractRole(authentication), pollId)
        ));
    }

    private RoleType extractRole(Authentication authentication) {
        boolean admin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> ADMIN_AUTHORITY.equals(authority.getAuthority()));
        return admin ? RoleType.ADMIN : RoleType.STUDENT;
    }
}
