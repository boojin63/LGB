package com.LGB.domain.poll.dto;

import com.LGB.domain.poll.entity.Poll;
import com.LGB.domain.poll.entity.PollStatus;
import java.time.LocalDateTime;

public record PollListResponse(
        Long id,
        String title,
        PollStatus status,
        boolean anonymous,
        boolean resultVisible,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        boolean hasVoted,
        LocalDateTime createdAt
) {
    public static PollListResponse from(Poll poll, boolean hasVoted) {
        return new PollListResponse(
                poll.getId(),
                poll.getTitle(),
                poll.getStatus(),
                poll.isAnonymous(),
                poll.isResultVisible(),
                poll.getStartsAt(),
                poll.getEndsAt(),
                hasVoted,
                poll.getCreatedAt()
        );
    }
}
