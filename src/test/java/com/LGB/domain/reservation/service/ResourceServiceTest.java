package com.LGB.domain.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.LGB.domain.reservation.dto.CreateResourceRequest;
import com.LGB.domain.reservation.dto.ResourceResponse;
import com.LGB.domain.reservation.dto.UpdateResourceActiveRequest;
import com.LGB.domain.reservation.dto.UpdateResourceRequest;
import com.LGB.domain.reservation.entity.Resource;
import com.LGB.domain.reservation.entity.ResourceType;
import com.LGB.domain.reservation.repository.ResourceRepository;
import com.LGB.global.exception.CustomException;
import com.LGB.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ResourceService resourceService;

    @Test
    void createResourceAsActive() {
        CreateResourceRequest request = new CreateResourceRequest(
                "Projector",
                ResourceType.EQUIPMENT,
                "Portable projector",
                "Office"
        );
        when(resourceRepository.save(any(Resource.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResourceResponse response = resourceService.create(request);

        assertThat(response.name()).isEqualTo("Projector");
        assertThat(response.type()).isEqualTo(ResourceType.EQUIPMENT);
        assertThat(response.active()).isTrue();
    }

    @Test
    void getResourcesReturnsOnlyActiveResourcesByDefault() {
        Resource active = resource(1L, "Active room", ResourceType.ROOM, true);
        when(resourceRepository.findByActiveTrueOrderByCreatedAtDesc())
                .thenReturn(List.of(active));

        List<ResourceResponse> response = resourceService.getResources(null, false);

        assertThat(response).extracting(ResourceResponse::name).containsExactly("Active room");
        verify(resourceRepository).findByActiveTrueOrderByCreatedAtDesc();
        verify(resourceRepository, never()).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void getResourcesIncludesInactiveResourcesWhenRequested() {
        Resource active = resource(1L, "Active room", ResourceType.ROOM, true);
        Resource inactive = resource(2L, "Inactive room", ResourceType.ROOM, false);
        when(resourceRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(List.of(active, inactive));

        List<ResourceResponse> response = resourceService.getResources(null, true);

        assertThat(response).extracting(ResourceResponse::active).containsExactly(true, false);
        verify(resourceRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void getResourcesFiltersActiveResourcesByType() {
        Resource equipment = resource(1L, "Projector", ResourceType.EQUIPMENT, true);
        when(resourceRepository.findByTypeAndActiveTrueOrderByCreatedAtDesc(ResourceType.EQUIPMENT))
                .thenReturn(List.of(equipment));

        List<ResourceResponse> response = resourceService.getResources(ResourceType.EQUIPMENT, false);

        assertThat(response).extracting(ResourceResponse::type)
                .containsExactly(ResourceType.EQUIPMENT);
        verify(resourceRepository)
                .findByTypeAndActiveTrueOrderByCreatedAtDesc(ResourceType.EQUIPMENT);
    }

    @Test
    void getResourcesFiltersAllResourcesByTypeWhenIncludingInactive() {
        Resource inactive = resource(1L, "Old projector", ResourceType.EQUIPMENT, false);
        when(resourceRepository.findByTypeOrderByCreatedAtDesc(ResourceType.EQUIPMENT))
                .thenReturn(List.of(inactive));

        List<ResourceResponse> response = resourceService.getResources(ResourceType.EQUIPMENT, true);

        assertThat(response).singleElement().satisfies(resource -> {
            assertThat(resource.type()).isEqualTo(ResourceType.EQUIPMENT);
            assertThat(resource.active()).isFalse();
        });
        verify(resourceRepository).findByTypeOrderByCreatedAtDesc(ResourceType.EQUIPMENT);
    }

    @Test
    void getResourceReturnsDetail() {
        Resource resource = resource(1L, "Room 101", ResourceType.ROOM, true);
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));

        ResourceResponse response = resourceService.getResource(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Room 101");
    }

    @Test
    void getResourceRejectsMissingResource() {
        when(resourceRepository.findById(99L)).thenReturn(Optional.empty());

        assertResourceNotFound(() -> resourceService.getResource(99L));
    }

    @Test
    void updateResourceChangesOnlyProvidedFields() {
        Resource resource = resource(1L, "Old name", ResourceType.ROOM, true);
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));

        ResourceResponse response = resourceService.update(
                1L,
                new UpdateResourceRequest("New name", null, null, "Second floor")
        );

        assertThat(response.name()).isEqualTo("New name");
        assertThat(response.type()).isEqualTo(ResourceType.ROOM);
        assertThat(response.description()).isEqualTo("Description");
        assertThat(response.location()).isEqualTo("Second floor");
        verify(resourceRepository).flush();
    }

    @Test
    void updateResourceRejectsEmptyRequest() {
        assertInvalidRequest(() -> resourceService.update(
                1L,
                new UpdateResourceRequest(null, null, null, null)
        ));
        verify(resourceRepository, never()).findById(1L);
    }

    @Test
    void updateResourceRejectsBlankName() {
        assertInvalidRequest(() -> resourceService.update(
                1L,
                new UpdateResourceRequest(" ", null, null, null)
        ));
        verify(resourceRepository, never()).findById(1L);
    }

    @Test
    void updateResourceRejectsMissingResource() {
        when(resourceRepository.findById(99L)).thenReturn(Optional.empty());

        assertResourceNotFound(() -> resourceService.update(
                99L,
                new UpdateResourceRequest("New name", null, null, null)
        ));
    }

    @Test
    void updateActiveChangesResourceToInactive() {
        Resource resource = resource(1L, "Room 101", ResourceType.ROOM, true);
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));

        ResourceResponse response = resourceService.updateActive(
                1L,
                new UpdateResourceActiveRequest(false)
        );

        assertThat(response.active()).isFalse();
        verify(resourceRepository).flush();
    }

    @Test
    void updateActiveChangesResourceToActive() {
        Resource resource = resource(1L, "Room 101", ResourceType.ROOM, false);
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));

        ResourceResponse response = resourceService.updateActive(
                1L,
                new UpdateResourceActiveRequest(true)
        );

        assertThat(response.active()).isTrue();
        verify(resourceRepository).flush();
    }

    @Test
    void updateActiveRejectsMissingResource() {
        when(resourceRepository.findById(99L)).thenReturn(Optional.empty());

        assertResourceNotFound(() -> resourceService.updateActive(
                99L,
                new UpdateResourceActiveRequest(false)
        ));
        verify(resourceRepository, never()).flush();
    }

    private Resource resource(Long id, String name, ResourceType type, boolean active) {
        Resource resource = new Resource(name, type, "Description", "First floor");
        ReflectionTestUtils.setField(resource, "id", id);
        ReflectionTestUtils.setField(resource, "active", active);
        ReflectionTestUtils.setField(resource, "createdAt", LocalDateTime.of(2026, 6, 1, 10, 0));
        ReflectionTestUtils.setField(resource, "updatedAt", LocalDateTime.of(2026, 6, 1, 10, 0));
        return resource;
    }

    private void assertResourceNotFound(Runnable action) {
        assertThatThrownBy(action::run)
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.RESOURCE_NOT_FOUND.getMessage());
    }

    private void assertInvalidRequest(Runnable action) {
        assertThatThrownBy(action::run)
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_RESOURCE_REQUEST.getMessage());
    }
}
