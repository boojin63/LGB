package com.LGB.domain.reservation.dto;

import com.LGB.domain.reservation.entity.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateResourceRequest(
        @NotBlank @Size(max = 150) String name,
        @NotNull ResourceType type,
        @Size(max = 10000) String description,
        @Size(max = 200) String location
) {
}
