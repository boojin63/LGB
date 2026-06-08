package com.LGB.domain.poll.dto;

import java.util.List;
import org.springframework.data.domain.Page;

public record PollPageResponse(
        List<PollListResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
    public static PollPageResponse from(Page<PollListResponse> polls) {
        return new PollPageResponse(
                polls.getContent(),
                polls.getNumber(),
                polls.getSize(),
                polls.getTotalElements(),
                polls.getTotalPages(),
                polls.isFirst(),
                polls.isLast()
        );
    }
}
