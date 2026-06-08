package com.LGB.domain.reservation.dto;

import com.LGB.domain.reservation.entity.ResourceType;
import jakarta.validation.constraints.Size;

public record UpdateResourceRequest(
        @Size(max = 150) String name,
        ResourceType type,
        @Size(max = 10000) String description,
        @Size(max = 200) String location
) {
}
