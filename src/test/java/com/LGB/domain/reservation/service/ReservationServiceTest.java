package com.LGB.domain.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.LGB.domain.reservation.dto.CreateReservationRequest;
import com.LGB.domain.reservation.dto.RejectReservationRequest;
import com.LGB.domain.reservation.dto.ReservationPageResponse;
import com.LGB.domain.reservation.dto.ReservationResponse;
import com.LGB.domain.reservation.entity.Reservation;
import com.LGB.domain.reservation.entity.ReservationStatus;
import com.LGB.domain.reservation.entity.Resource;
import com.LGB.domain.reservation.entity.ResourceType;
import com.LGB.domain.reservation.repository.ReservationRepository;
import com.LGB.domain.reservation.repository.ResourceRepository;
import com.LGB.domain.user.entity.User;
import com.LGB.domain.user.repository.UserRepository;
import com.LGB.global.exception.CustomException;
import com.LGB.global.exception.ErrorCode;
import com.LGB.global.security.RoleType;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");
    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-06-05T00:00:00Z"),
            ZONE
    );
    private static final LocalDateTime START = LocalDateTime.of(2026, 6, 6, 10, 0);
    private static final LocalDateTime END = START.plusHours(2);

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private UserRepository userRepository;

    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationService(
                reservationRepository,
                resourceRepository,
                userRepository,
                CLOCK
        );
    }

    @Test
    void createReservationAsStudent() {
        User student = user(1L, "Student", RoleType.STUDENT, true);
        Resource resource = resource(10L, true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(resourceRepository.findById(10L)).thenReturn(Optional.of(resource));
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReservationResponse response = reservationService.create(1L, request(START, END));

        assertThat(response.status()).isEqualTo(ReservationStatus.PENDING);
        assertThat(response.requester().id()).isEqualTo(1L);
        assertThat(response.resource().id()).isEqualTo(10L);
    }

    @Test
    void createReservationRejectsAdminRequester() {
        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user(2L, "Admin", RoleType.ADMIN, true)));

        assertError(
                () -> reservationService.create(2L, request(START, END)),
                ErrorCode.FORBIDDEN
        );
        verify(resourceRepository, never()).findById(any(Long.class));
    }

    @Test
    void createReservationRejectsMissingRequester() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertError(
                () -> reservationService.create(99L, request(START, END)),
                ErrorCode.NOT_FOUND
        );
    }

    @Test
    void createReservationRejectsInactiveRequester() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user(1L, "Student", RoleType.STUDENT, false)));

        assertError(
                () -> reservationService.create(1L, request(START, END)),
                ErrorCode.NOT_FOUND
        );
    }

    @Test
    void createReservationRejectsMissingResource() {
        stubActiveStudent();
        when(resourceRepository.findById(10L)).thenReturn(Optional.empty());

        assertError(
                () -> reservationService.create(1L, request(START, END)),
                ErrorCode.RESOURCE_NOT_FOUND
        );
    }

    @Test
    void createReservationRejectsInactiveResource() {
        stubActiveStudent();
        when(resourceRepository.findById(10L)).thenReturn(Optional.of(resource(10L, false)));

        assertError(
                () -> reservationService.create(1L, request(START, END)),
                ErrorCode.RESOURCE_INACTIVE
        );
    }

    @Test
    void createReservationRejectsEndBeforeStart() {
        assertInvalidPeriod(START, START.minusMinutes(1));
    }

    @Test
    void createReservationRejectsEqualStartAndEnd() {
        assertInvalidPeriod(START, START);
    }

    @Test
    void createReservationRejectsPastStart() {
        assertInvalidPeriod(LocalDateTime.of(2026, 6, 5, 8, 59), START);
    }

    @Test
    void createReservationRejectsDurationOverTwentyFourHours() {
        assertInvalidPeriod(START, START.plusHours(24).plusNanos(1));
    }

    @Test
    void createReservationAllowsPendingRequestWhenApprovedReservationMayOverlap() {
        stubActiveStudent();
        when(resourceRepository.findById(10L)).thenReturn(Optional.of(resource(10L, true)));
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReservationResponse response = reservationService.create(1L, request(START, END));

        assertThat(response.status()).isEqualTo(ReservationStatus.PENDING);
        verify(reservationRepository, never()).existsApprovedOverlapExcludingSelf(
                any(Long.class),
                any(Long.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );
    }

    @Test
    void getMyReservationsReturnsRequesterReservations() {
        Reservation reservation = reservation(100L, student(), resource(10L, true), ReservationStatus.PENDING);
        Pageable pageable = PageRequest.of(0, 10);
        when(reservationRepository.findByRequesterIdOrderByCreatedAtDesc(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(reservation), pageable, 1));

        ReservationPageResponse response = reservationService.getMyReservations(1L, null, 0, 10);

        assertThat(response.content()).extracting(ReservationResponse::id).containsExactly(100L);
        verify(reservationRepository).findByRequesterIdOrderByCreatedAtDesc(1L, pageable);
    }

    @Test
    void getMyReservationsFiltersByStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        when(reservationRepository.findByRequesterIdAndStatusOrderByCreatedAtDesc(
                1L,
                ReservationStatus.APPROVED,
                pageable
        )).thenReturn(new PageImpl<>(List.of(), pageable, 0));

        reservationService.getMyReservations(1L, ReservationStatus.APPROVED, 0, 10);

        verify(reservationRepository).findByRequesterIdAndStatusOrderByCreatedAtDesc(
                1L,
                ReservationStatus.APPROVED,
                pageable
        );
    }

    @Test
    void getAdminReservationsReturnsAllReservations() {
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(reservationRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        reservationService.getAdminReservations(null, null, 0, 10);

        verify(reservationRepository).findAll(pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getSort().getOrderFor("createdAt").isAscending())
                .isTrue();
    }

    @Test
    void getAdminReservationsFiltersByStatusAndResource() {
        Pageable pageable = PageRequest.of(
                0,
                10,
                org.springframework.data.domain.Sort.by(
                        org.springframework.data.domain.Sort.Direction.ASC,
                        "createdAt"
                )
        );
        when(reservationRepository.findByResourceIdAndStatusOrderByCreatedAtAsc(
                10L,
                ReservationStatus.PENDING,
                pageable
        )).thenReturn(new PageImpl<>(List.of(), pageable, 0));

        reservationService.getAdminReservations(ReservationStatus.PENDING, 10L, 0, 10);

        verify(reservationRepository).findByResourceIdAndStatusOrderByCreatedAtAsc(
                10L,
                ReservationStatus.PENDING,
                pageable
        );
    }

    @Test
    void getReservationAllowsStudentOwner() {
        Reservation reservation = reservation(100L, student(), resource(10L, true), ReservationStatus.PENDING);
        when(reservationRepository.findById(100L)).thenReturn(Optional.of(reservation));

        ReservationResponse response = reservationService.getReservation(
                1L,
                RoleType.STUDENT,
                100L
        );

        assertThat(response.id()).isEqualTo(100L);
    }

    @Test
    void getReservationRejectsOtherStudent() {
        Reservation reservation = reservation(100L, student(), resource(10L, true), ReservationStatus.PENDING);
        when(reservationRepository.findById(100L)).thenReturn(Optional.of(reservation));

        assertError(
                () -> reservationService.getReservation(2L, RoleType.STUDENT, 100L),
                ErrorCode.RESERVATION_ACCESS_DENIED
        );
    }

    @Test
    void getReservationAllowsAdmin() {
        Reservation reservation = reservation(100L, student(), resource(10L, true), ReservationStatus.PENDING);
        when(reservationRepository.findById(100L)).thenReturn(Optional.of(reservation));

        ReservationResponse response = reservationService.getReservation(2L, RoleType.ADMIN, 100L);

        assertThat(response.id()).isEqualTo(100L);
    }

    @Test
    void getReservationRejectsMissingReservation() {
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        assertError(
                () -> reservationService.getReservation(1L, RoleType.STUDENT, 999L),
                ErrorCode.RESERVATION_NOT_FOUND
        );
    }

    @Test
    void approveReservationLocksResourceAndApproves() {
        User admin = admin();
        Resource resource = resource(10L, true);
        Reservation reservation = reservation(100L, student(), resource, ReservationStatus.PENDING);
        stubAdminAndReservation(admin, reservation);
        when(resourceRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(resource));
        when(reservationRepository.existsApprovedOverlapExcludingSelf(10L, 100L, START, END))
                .thenReturn(false);

        ReservationResponse response = reservationService.approve(2L, 100L);

        assertThat(response.status()).isEqualTo(ReservationStatus.APPROVED);
        assertThat(response.decidedBy().id()).isEqualTo(2L);
        assertThat(response.decidedAt()).isNotNull();
        verify(resourceRepository).findByIdForUpdate(10L);
        verify(reservationRepository).existsApprovedOverlapExcludingSelf(10L, 100L, START, END);
        verify(reservationRepository).flush();
    }

    @Test
    void approveReservationRejectsApprovedOverlap() {
        Resource resource = resource(10L, true);
        Reservation reservation = reservation(100L, student(), resource, ReservationStatus.PENDING);
        stubAdminAndReservation(admin(), reservation);
        when(resourceRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(resource));
        when(reservationRepository.existsApprovedOverlapExcludingSelf(10L, 100L, START, END))
                .thenReturn(true);

        assertError(
                () -> reservationService.approve(2L, 100L),
                ErrorCode.RESERVATION_TIME_CONFLICT
        );
        verify(reservationRepository, never()).flush();
    }

    @Test
    void approveReservationRejectsAlreadyApprovedReservation() {
        Reservation reservation = reservation(
                100L,
                student(),
                resource(10L, true),
                ReservationStatus.APPROVED
        );
        stubAdminAndReservation(admin(), reservation);

        assertError(
                () -> reservationService.approve(2L, 100L),
                ErrorCode.RESERVATION_ALREADY_DECIDED
        );
        verify(resourceRepository, never()).findByIdForUpdate(any(Long.class));
    }

    @Test
    void approveReservationRejectsAlreadyRejectedReservation() {
        Reservation reservation = reservation(
                100L,
                student(),
                resource(10L, true),
                ReservationStatus.REJECTED
        );
        stubAdminAndReservation(admin(), reservation);

        assertError(
                () -> reservationService.approve(2L, 100L),
                ErrorCode.RESERVATION_ALREADY_DECIDED
        );
    }

    @Test
    void rejectReservationAsAdmin() {
        User admin = admin();
        Reservation reservation = reservation(
                100L,
                student(),
                resource(10L, true),
                ReservationStatus.PENDING
        );
        stubAdminAndReservation(admin, reservation);

        ReservationResponse response = reservationService.reject(
                2L,
                100L,
                new RejectReservationRequest("Unavailable")
        );

        assertThat(response.status()).isEqualTo(ReservationStatus.REJECTED);
        assertThat(response.rejectReason()).isEqualTo("Unavailable");
        assertThat(response.decidedBy().id()).isEqualTo(2L);
        verify(reservationRepository).flush();
    }

    @Test
    void rejectReservationRejectsBlankReason() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin()));

        assertError(
                () -> reservationService.reject(2L, 100L, new RejectReservationRequest(" ")),
                ErrorCode.INVALID_RESERVATION_REQUEST
        );
        verify(reservationRepository, never()).findById(100L);
    }

    @Test
    void rejectReservationRejectsNullReason() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin()));

        assertError(
                () -> reservationService.reject(2L, 100L, new RejectReservationRequest(null)),
                ErrorCode.INVALID_RESERVATION_REQUEST
        );
    }

    @Test
    void rejectReservationRejectsAlreadyApprovedReservation() {
        Reservation reservation = reservation(
                100L,
                student(),
                resource(10L, true),
                ReservationStatus.APPROVED
        );
        stubAdminAndReservation(admin(), reservation);

        assertError(
                () -> reservationService.reject(
                        2L,
                        100L,
                        new RejectReservationRequest("Unavailable")
                ),
                ErrorCode.RESERVATION_ALREADY_DECIDED
        );
    }

    @Test
    void approveReservationRejectsMissingReservation() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin()));
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        assertError(
                () -> reservationService.approve(2L, 999L),
                ErrorCode.RESERVATION_NOT_FOUND
        );
    }

    @Test
    void rejectReservationRejectsMissingReservation() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin()));
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        assertError(
                () -> reservationService.reject(
                        2L,
                        999L,
                        new RejectReservationRequest("Unavailable")
                ),
                ErrorCode.RESERVATION_NOT_FOUND
        );
    }

    @Test
    void paginationRejectsNegativePage() {
        assertError(
                () -> reservationService.getMyReservations(1L, null, -1, 10),
                ErrorCode.INVALID_RESERVATION_REQUEST
        );
    }

    @Test
    void paginationRejectsZeroSize() {
        assertError(
                () -> reservationService.getAdminReservations(null, null, 0, 0),
                ErrorCode.INVALID_RESERVATION_REQUEST
        );
    }

    @Test
    void paginationLimitsSizeToFifty() {
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(reservationRepository.findByRequesterIdOrderByCreatedAtDesc(
                org.mockito.ArgumentMatchers.eq(1L),
                any(Pageable.class)
        )).thenReturn(new PageImpl<>(List.of()));

        reservationService.getMyReservations(1L, null, 0, 100);

        verify(reservationRepository).findByRequesterIdOrderByCreatedAtDesc(
                org.mockito.ArgumentMatchers.eq(1L),
                pageableCaptor.capture()
        );
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(50);
    }

    private void assertInvalidPeriod(LocalDateTime startAt, LocalDateTime endAt) {
        stubActiveStudent();
        when(resourceRepository.findById(10L)).thenReturn(Optional.of(resource(10L, true)));

        assertError(
                () -> reservationService.create(1L, request(startAt, endAt)),
                ErrorCode.INVALID_RESERVATION_PERIOD
        );
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    private void stubActiveStudent() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student()));
    }

    private void stubAdminAndReservation(User admin, Reservation reservation) {
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(reservationRepository.findById(100L)).thenReturn(Optional.of(reservation));
    }

    private CreateReservationRequest request(LocalDateTime startAt, LocalDateTime endAt) {
        return new CreateReservationRequest(10L, startAt, endAt, "Team project");
    }

    private User student() {
        return user(1L, "Student", RoleType.STUDENT, true);
    }

    private User admin() {
        return user(2L, "Admin", RoleType.ADMIN, true);
    }

    private User user(Long id, String name, RoleType role, boolean active) {
        User user = new User(name.toLowerCase() + "@lgb.local", "encoded", name, role);
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "active", active);
        return user;
    }

    private Resource resource(Long id, boolean active) {
        Resource resource = new Resource("Room 101", ResourceType.ROOM, "Description", "First floor");
        ReflectionTestUtils.setField(resource, "id", id);
        ReflectionTestUtils.setField(resource, "active", active);
        ReflectionTestUtils.setField(resource, "createdAt", START.minusDays(10));
        ReflectionTestUtils.setField(resource, "updatedAt", START.minusDays(10));
        return resource;
    }

    private Reservation reservation(
            Long id,
            User requester,
            Resource resource,
            ReservationStatus status
    ) {
        Reservation reservation = new Reservation(resource, requester, START, END, "Team project");
        ReflectionTestUtils.setField(reservation, "id", id);
        ReflectionTestUtils.setField(reservation, "status", status);
        ReflectionTestUtils.setField(reservation, "createdAt", START.minusDays(1));
        ReflectionTestUtils.setField(reservation, "updatedAt", START.minusDays(1));
        return reservation;
    }

    private void assertError(Runnable action, ErrorCode errorCode) {
        assertThatThrownBy(action::run)
                .isInstanceOf(CustomException.class)
                .hasMessage(errorCode.getMessage());
    }
}
