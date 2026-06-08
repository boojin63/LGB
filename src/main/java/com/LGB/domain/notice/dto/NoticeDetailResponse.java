package com.LGB.domain.notice.dto;

import com.LGB.domain.notice.entity.Notice;
import java.time.LocalDateTime;

public record NoticeDetailResponse(
        Long id,
        String title,
        String content,
        NoticeAuthorResponse author,
        boolean pinned,
        long viewCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static NoticeDetailResponse from(Notice notice) {
        return new NoticeDetailResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                NoticeAuthorResponse.from(notice.getAuthor()),
                notice.isPinned(),
                notice.getViewCount(),
                notice.getCreatedAt(),
                notice.getUpdatedAt()
        );
    }
}
