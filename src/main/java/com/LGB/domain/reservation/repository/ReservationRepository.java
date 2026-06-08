package com.LGB.domain.reservation.repository;

import com.LGB.domain.reservation.entity.Reservation;
import com.LGB.domain.reservation.entity.ReservationStatus;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Page<Reservation> findByRequesterIdOrderByCreatedAtDesc(Long requesterId, Pageable pageable);

    Page<Reservation> findByRequesterIdAndStatusOrderByCreatedAtDesc(
            Long requesterId,
            ReservationStatus status,
            Pageable pageable
    );

    Page<Reservation> findByStatusOrderByCreatedAtAsc(
            ReservationStatus status,
            Pageable pageable
    );

    Page<Reservation> findByResourceIdOrderByCreatedAtAsc(Long resourceId, Pageable pageable);

    Page<Reservation> findByResourceIdAndStatusOrderByCreatedAtAsc(
            Long resourceId,
            ReservationStatus status,
            Pageable pageable
    );

    @Query("""
            select count(reservation) > 0
            from Reservation reservation
            where reservation.resource.id = :resourceId
              and reservation.status = com.LGB.domain.reservation.entity.ReservationStatus.APPROVED
              and reservation.startAt < :requestedEndAt
              and reservation.endAt > :requestedStartAt
              and (:excludedReservationId is null or reservation.id <> :excludedReservationId)
            """)
    boolean existsApprovedOverlapExcludingSelf(
            @Param("resourceId") Long resourceId,
            @Param("excludedReservationId") Long excludedReservationId,
            @Param("requestedStartAt") LocalDateTime requestedStartAt,
            @Param("requestedEndAt") LocalDateTime requestedEndAt
    );
}
