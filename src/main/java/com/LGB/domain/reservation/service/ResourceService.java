package com.LGB.domain.reservation.service;

import com.LGB.domain.reservation.dto.CreateResourceRequest;
import com.LGB.domain.reservation.dto.ResourceResponse;
import com.LGB.domain.reservation.dto.UpdateResourceActiveRequest;
import com.LGB.domain.reservation.dto.UpdateResourceRequest;
import com.LGB.domain.reservation.entity.Resource;
import com.LGB.domain.reservation.entity.ResourceType;
import com.LGB.domain.reservation.repository.ResourceRepository;
import com.LGB.global.exception.CustomException;
import com.LGB.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResourceService {

    private final ResourceRepository resourceRepository;

    @Transactional
    public ResourceResponse create(CreateResourceRequest request) {
        Resource resource = new Resource(
                request.name(),
                request.type(),
                request.description(),
                request.location()
        );

        return ResourceResponse.from(resourceRepository.save(resource));
    }

    public List<ResourceResponse> getResources(ResourceType type, boolean includeInactive) {
        List<Resource> resources;

        if (includeInactive && type == null) {
            resources = resourceRepository.findAllByOrderByCreatedAtDesc();
        } else if (includeInactive) {
            resources = resourceRepository.findByTypeOrderByCreatedAtDesc(type);
        } else if (type == null) {
            resources = resourceRepository.findByActiveTrueOrderByCreatedAtDesc();
        } else {
            resources = resourceRepository.findByTypeAndActiveTrueOrderByCreatedAtDesc(type);
        }

        return resources.stream()
                .map(ResourceResponse::from)
                .toList();
    }

    public ResourceResponse getResource(Long resourceId) {
        return ResourceResponse.from(findResource(resourceId));
    }

    @Transactional
    public ResourceResponse update(Long resourceId, UpdateResourceRequest request) {
        validateUpdateRequest(request);

        Resource resource = findResource(resourceId);
        resource.update(request.name(), request.type(), request.description(), request.location());
        resourceRepository.flush();

        return ResourceResponse.from(resource);
    }

    @Transactional
    public ResourceResponse updateActive(Long resourceId, UpdateResourceActiveRequest request) {
        Resource resource = findResource(resourceId);
        resource.updateActive(request.active());
        resourceRepository.flush();

        return ResourceResponse.from(resource);
    }

    private Resource findResource(Long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    private void validateUpdateRequest(UpdateResourceRequest request) {
        if (request.name() == null
                && request.type() == null
                && request.description() == null
                && request.location() == null) {
            throw new CustomException(ErrorCode.INVALID_RESOURCE_REQUEST);
        }
        if (request.name() != null && request.name().isBlank()) {
            throw new CustomException(ErrorCode.INVALID_RESOURCE_REQUEST);
        }
    }
}
