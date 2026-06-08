package com.LGB.domain.reservation.dto;

import com.LGB.domain.reservation.entity.Resource;
import com.LGB.domain.reservation.entity.ResourceType;

public record ReservationResourceResponse(
        Long id,
        String name,
        ResourceType type,
        String location
) {
    public static ReservationResourceResponse from(Resource resource) {
        return new ReservationResourceResponse(
                resource.getId(),
                resource.getName(),
                resource.getType(),
                resource.getLocation()
        );
    }
}
