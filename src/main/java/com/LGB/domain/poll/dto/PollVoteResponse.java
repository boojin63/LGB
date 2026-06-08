package com.LGB.domain.poll.dto;

import java.time.LocalDateTime;

public record PollVoteResponse(
        Long pollId,
        Long optionId,
        LocalDateTime votedAt
) {
}
