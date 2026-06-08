package com.LGB.domain.reservation.dto;

import com.LGB.domain.reservation.entity.Resource;
import com.LGB.domain.reservation.entity.ResourceType;
import java.time.LocalDateTime;

public record ResourceResponse(
        Long id,
        String name,
        ResourceType type,
        String description,
        String location,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ResourceResponse from(Resource resource) {
        return new ResourceResponse(
                resource.getId(),
                resource.getName(),
                resource.getType(),
                resource.getDescription(),
                resource.getLocation(),
                resource.isActive(),
                resource.getCreatedAt(),
                resource.getUpdatedAt()
        );
    }
}
