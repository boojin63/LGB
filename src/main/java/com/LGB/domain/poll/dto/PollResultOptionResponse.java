package com.LGB.domain.poll.dto;

public record PollResultOptionResponse(
        Long optionId,
        String text,
        int displayOrder,
        long voteCount,
        double percentage
) {
}
