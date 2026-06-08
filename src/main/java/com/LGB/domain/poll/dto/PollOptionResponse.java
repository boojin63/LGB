package com.LGB.domain.poll.dto;

import com.LGB.domain.poll.entity.PollOption;

public record PollOptionResponse(
        Long id,
        String text,
        int displayOrder
) {
    public static PollOptionResponse from(PollOption option) {
        return new PollOptionResponse(
                option.getId(),
                option.getText(),
                option.getDisplayOrder()
        );
    }
}
