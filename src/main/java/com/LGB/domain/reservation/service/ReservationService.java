package com.LGB.domain.reservation.service;

import com.LGB.domain.reservation.dto.CreateReservationRequest;
import com.LGB.domain.reservation.dto.RejectReservationRequest;
import com.LGB.domain.reservation.dto.ReservationPageResponse;
import com.LGB.domain.reservation.dto.ReservationResponse;
import com.LGB.domain.reservation.entity.Reservation;
import com.LGB.domain.reservation.entity.ReservationStatus;
import com.LGB.domain.reservation.entity.Resource;
import com.LGB.domain.reservation.repository.ReservationRepository;
import com.LGB.domain.reservation.repository.ResourceRepository;
import com.LGB.domain.user.entity.User;
import com.LGB.domain.user.repository.UserRepository;
import com.LGB.global.exception.CustomException;
import com.LGB.global.exception.ErrorCode;
import com.LGB.global.security.RoleType;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReservationService {

    private static final int MAX_PAGE_SIZE = 50;
    private static final Duration MAX_RESERVATION_DURATION = Duration.ofHours(24);

    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;
    private final Clock clock;

    @Autowired
    public ReservationService(
            ReservationRepository reservationRepository,
            ResourceRepository resourceRepository,
            UserRepository userRepository
    ) {
        this(reservationRepository, resourceRepository, userRepository, Clock.systemDefaultZone());
    }

    ReservationService(
            ReservationRepository reservationRepository,
            ResourceRepository resourceRepository,
            UserRepository userRepository,
            Clock clock
    ) {
        this.reservationRepository = reservationRepository;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
        this.clock = clock;
    }

    @Transactional
    public ReservationResponse create(Long requesterId, CreateReservationRequest request) {
        User requester = findActiveUser(requesterId);
        if (requester.getRole() != RoleType.STUDENT) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        validateCreateRequest(request);
        Resource resource = resourceRepository.findById(request.resourceId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        if (!resource.isActive()) {
            throw new CustomException(ErrorCode.RESOURCE_INACTIVE);
        }

        validateReservationPeriod(request.startAt(), request.endAt());
        Reservation reservation = new Reservation(
                resource,
                requester,
                request.startAt(),
                request.endAt(),
                request.purpose()
        );

        return ReservationResponse.from(reservationRepository.save(reservation));
    }

    public ReservationPageResponse getMyReservations(
            Long requesterId,
            ReservationStatus status,
            int page,
            int size
    ) {
        int limitedSize = validateAndLimitPage(page, size);
        PageRequest pageable = PageRequest.of(page, limitedSize);
        Page<Reservation> reservations = status == null
                ? reservationRepository.findByRequesterIdOrderByCreatedAtDesc(requesterId, pageable)
                : reservationRepository.findByRequesterIdAndStatusOrderByCreatedAtDesc(
                        requesterId,
                        status,
                        pageable
                );

        return ReservationPageResponse.from(reservations.map(ReservationResponse::from));
    }

    public ReservationPageResponse getAdminReservations(
            ReservationStatus status,
            Long resourceId,
            int page,
            int size
    ) {
        int limitedSize = validateAndLimitPage(page, size);
        PageRequest pageable = PageRequest.of(
                page,
                limitedSize,
                Sort.by(Sort.Direction.ASC, "createdAt")
        );
        Page<Reservation> reservations;

        if (status == null && resourceId == null) {
            reservations = reservationRepository.findAll(pageable);
        } else if (status != null && resourceId == null) {
            reservations = reservationRepository.findByStatusOrderByCreatedAtAsc(status, pageable);
        } else if (status == null) {
            reservations = reservationRepository.findByResourceIdOrderByCreatedAtAsc(resourceId, pageable);
        } else {
            reservations = reservationRepository.findByResourceIdAndStatusOrderByCreatedAtAsc(
                    resourceId,
                    status,
                    pageable
            );
        }

        return ReservationPageResponse.from(reservations.map(ReservationResponse::from));
    }

    public ReservationResponse getReservation(
            Long currentUserId,
            RoleType role,
            Long reservationId
    ) {
        Reservation reservation = findReservation(reservationId);
        if (role != RoleType.ADMIN
                && !Objects.equals(reservation.getRequester().getId(), currentUserId)) {
            throw new CustomException(ErrorCode.RESERVATION_ACCESS_DENIED);
        }

        return ReservationResponse.from(reservation);
    }

    @Transactional
    public ReservationResponse approve(Long adminId, Long reservationId) {
        User admin = findActiveAdmin(adminId);
        Reservation reservation = findReservation(reservationId);
        validatePending(reservation);

        Long resourceId = reservation.getResource().getId();
        resourceRepository.findByIdForUpdate(resourceId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        boolean conflict = reservationRepository.existsApprovedOverlapExcludingSelf(
                resourceId,
                reservation.getId(),
                reservation.getStartAt(),
                reservation.getEndAt()
        );
        if (conflict) {
            throw new CustomException(ErrorCode.RESERVATION_TIME_CONFLICT);
        }

        reservation.approve(admin);
        reservationRepository.flush();
        return ReservationResponse.from(reservation);
    }

    @Transactional
    public ReservationResponse reject(
            Long adminId,
            Long reservationId,
            RejectReservationRequest request
    ) {
        User admin = findActiveAdmin(adminId);
        validateRejectRequest(request);

        Reservation reservation = findReservation(reservationId);
        validatePending(reservation);
        reservation.reject(admin, request.rejectReason());
        reservationRepository.flush();

        return ReservationResponse.from(reservation);
    }

    private User findActiveUser(Long userId) {
        return userRepository.findById(userId)
                .filter(User::isActive)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }

    private User findActiveAdmin(Long adminId) {
        User admin = findActiveUser(adminId);
        if (admin.getRole() != RoleType.ADMIN) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        return admin;
    }

    private Reservation findReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
    }

    private void validateCreateRequest(CreateReservationRequest request) {
        if (request == null
                || request.resourceId() == null
                || request.purpose() == null
                || request.purpose().isBlank()) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_REQUEST);
        }
    }

    private void validateRejectRequest(RejectReservationRequest request) {
        if (request == null
                || request.rejectReason() == null
                || request.rejectReason().isBlank()) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_REQUEST);
        }
    }

    private void validateReservationPeriod(LocalDateTime startAt, LocalDateTime endAt) {
        if (startAt == null || endAt == null || !endAt.isAfter(startAt)) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_PERIOD);
        }
        if (startAt.isBefore(LocalDateTime.now(clock))) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_PERIOD);
        }
        if (Duration.between(startAt, endAt).compareTo(MAX_RESERVATION_DURATION) > 0) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_PERIOD);
        }
    }

    private int validateAndLimitPage(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_REQUEST);
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }

    private void validatePending(Reservation reservation) {
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new CustomException(ErrorCode.RESERVATION_ALREADY_DECIDED);
        }
    }
}
