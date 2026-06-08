package com.LGB.domain.poll.dto;

import com.LGB.domain.poll.entity.PollStatus;
import java.time.LocalDateTime;
import java.util.List;

public record PollResultResponse(
        Long pollId,
        String title,
        long totalVotes,
        List<PollResultOptionResponse> options,
        boolean anonymous,
        boolean resultVisible,
        PollStatus status,
        LocalDateTime startsAt,
        LocalDateTime endsAt
) {
}
