package com.LGB.domain.notice.dto;

import jakarta.validation.constraints.Size;

public record UpdateNoticeRequest(
        @Size(max = 200) String title,
        @Size(max = 10000) String content,
        Boolean pinned
) {
}
