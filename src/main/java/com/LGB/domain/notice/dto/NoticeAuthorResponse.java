package com.LGB.domain.notice.dto;

import com.LGB.domain.user.entity.User;

public record NoticeAuthorResponse(
        Long id,
        String name
) {
    public static NoticeAuthorResponse from(User author) {
        return new NoticeAuthorResponse(author.getId(), author.getName());
    }
}
