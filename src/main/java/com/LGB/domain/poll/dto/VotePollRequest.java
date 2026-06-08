package com.LGB.domain.poll.dto;

import jakarta.validation.constraints.NotNull;

public record VotePollRequest(
        @NotNull Long optionId
) {
}
