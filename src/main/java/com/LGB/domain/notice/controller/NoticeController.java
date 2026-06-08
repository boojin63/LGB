package com.LGB.domain.notice.controller;

import com.LGB.domain.notice.dto.CreateNoticeRequest;
import com.LGB.domain.notice.dto.NoticeDetailResponse;
import com.LGB.domain.notice.dto.NoticePageResponse;
import com.LGB.domain.notice.dto.UpdateNoticeRequest;
import com.LGB.domain.notice.service.NoticeService;
import com.LGB.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping
    public ResponseEntity<ApiResponse<NoticeDetailResponse>> create(
            @AuthenticationPrincipal Long authorId,
            @Valid @RequestBody CreateNoticeRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(noticeService.create(authorId, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<NoticePageResponse>> getNotices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(noticeService.getNotices(page, size)));
    }

    @GetMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeDetailResponse>> getNotice(
            @PathVariable Long noticeId
    ) {
        return ResponseEntity.ok(ApiResponse.success(noticeService.getNotice(noticeId)));
    }

    @PatchMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeDetailResponse>> update(
            @PathVariable Long noticeId,
            @Valid @RequestBody UpdateNoticeRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(noticeService.update(noticeId, request)));
    }

    @DeleteMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long noticeId) {
        noticeService.delete(noticeId);
        return ResponseEntity.ok(ApiResponse.emptySuccess());
    }
}
