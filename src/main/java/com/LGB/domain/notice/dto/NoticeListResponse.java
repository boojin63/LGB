package com.LGB.domain.notice.dto;

import com.LGB.domain.notice.entity.Notice;
import java.time.LocalDateTime;

public record NoticeListResponse(
        Long id,
        String title,
        String authorName,
        boolean pinned,
        long viewCount,
        LocalDateTime createdAt
) {
    public static NoticeListResponse from(Notice notice) {
        return new NoticeListResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getAuthor().getName(),
                notice.isPinned(),
                notice.getViewCount(),
                notice.getCreatedAt()
        );
    }
}
