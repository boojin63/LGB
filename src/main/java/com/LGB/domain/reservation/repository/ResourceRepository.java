package com.LGB.domain.reservation.repository;

import com.LGB.domain.reservation.entity.Resource;
import com.LGB.domain.reservation.entity.ResourceType;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

    List<Resource> findAllByOrderByCreatedAtDesc();

    List<Resource> findByTypeOrderByCreatedAtDesc(ResourceType type);

    List<Resource> findByActiveTrueOrderByCreatedAtDesc();

    List<Resource> findByTypeAndActiveTrueOrderByCreatedAtDesc(ResourceType type);

    Optional<Resource> findByIdAndActiveTrue(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select resource from Resource resource where resource.id = :id")
    Optional<Resource> findByIdForUpdate(@Param("id") Long id);
}
