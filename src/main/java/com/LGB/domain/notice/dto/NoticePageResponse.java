package com.LGB.domain.notice.dto;

import java.util.List;
import org.springframework.data.domain.Page;

public record NoticePageResponse(
        List<NoticeListResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
    public static NoticePageResponse from(Page<NoticeListResponse> notices) {
        return new NoticePageResponse(
                notices.getContent(),
                notices.getNumber(),
                notices.getSize(),
                notices.getTotalElements(),
                notices.getTotalPages(),
                notices.isFirst(),
                notices.isLast()
        );
    }
}
