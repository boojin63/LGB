package com.LGB.domain.poll.dto;

import com.LGB.domain.poll.entity.Poll;
import com.LGB.domain.poll.entity.PollOption;
import com.LGB.domain.poll.entity.PollStatus;
import java.time.LocalDateTime;
import java.util.List;

public record PollDetailResponse(
        Long id,
        String title,
        String description,
        PollStatus status,
        boolean anonymous,
        boolean resultVisible,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        List<PollOptionResponse> options,
        boolean hasVoted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PollDetailResponse from(
            Poll poll,
            List<PollOption> options,
            boolean hasVoted
    ) {
        return new PollDetailResponse(
                poll.getId(),
                poll.getTitle(),
                poll.getDescription(),
                poll.getStatus(),
                poll.isAnonymous(),
                poll.isResultVisible(),
                poll.getStartsAt(),
                poll.getEndsAt(),
                options.stream().map(PollOptionResponse::from).toList(),
                hasVoted,
                poll.getCreatedAt(),
                poll.getUpdatedAt()
        );
    }
}
