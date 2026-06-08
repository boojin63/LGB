package com.LGB.domain.poll.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.LGB.domain.poll.dto.CreatePollRequest;
import com.LGB.domain.poll.dto.PollDetailResponse;
import com.LGB.domain.poll.dto.PollPageResponse;
import com.LGB.domain.poll.dto.PollResultOptionResponse;
import com.LGB.domain.poll.dto.PollResultResponse;
import com.LGB.domain.poll.dto.PollVoteResponse;
import com.LGB.domain.poll.dto.UpdatePollRequest;
import com.LGB.domain.poll.dto.VotePollRequest;
import com.LGB.domain.poll.entity.Poll;
import com.LGB.domain.poll.entity.PollOption;
import com.LGB.domain.poll.entity.PollStatus;
import com.LGB.domain.poll.entity.PollVote;
import com.LGB.domain.poll.repository.PollOptionRepository;
import com.LGB.domain.poll.repository.PollRepository;
import com.LGB.domain.poll.repository.PollVoteRepository;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PollServiceTest {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");
    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-06-05T00:00:00Z"),
            ZONE
    );
    private static final LocalDateTime STARTS_AT = LocalDateTime.of(2026, 6, 5, 9, 0);
    private static final LocalDateTime ENDS_AT = LocalDateTime.of(2026, 6, 10, 18, 0);

    @Mock
    private PollRepository pollRepository;

    @Mock
    private PollOptionRepository pollOptionRepository;

    @Mock
    private PollVoteRepository pollVoteRepository;

    @Mock
    private UserRepository userRepository;

    private PollService pollService;

    @BeforeEach
    void setUp() {
        pollService = new PollService(
                pollRepository,
                pollOptionRepository,
                pollVoteRepository,
                userRepository,
                CLOCK
        );
    }

    @Test
    void createPollAsAdmin() {
        User admin = admin();
        CreatePollRequest request = createRequest(List.of("A", "B"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(pollRepository.save(any(Poll.class))).thenAnswer(invocation -> {
            Poll poll = invocation.getArgument(0);
            setPollFields(poll, 100L, PollStatus.OPEN);
            return poll;
        });
        when(pollOptionRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        PollDetailResponse response = pollService.create(1L, request);

        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.title()).isEqualTo("Poll");
        assertThat(response.status()).isEqualTo(PollStatus.OPEN);
        assertThat(response.anonymous()).isTrue();
        assertThat(response.resultVisible()).isFalse();
        assertThat(response.options()).extracting("displayOrder").containsExactly(1, 2);
        assertThat(response.hasVoted()).isFalse();
    }

    @Test
    void createPollRejectsStudentAdminId() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2L, "Student", RoleType.STUDENT, true)));

        assertError(() -> pollService.create(2L, createRequest(List.of("A", "B"))), ErrorCode.FORBIDDEN);
    }

    @Test
    void createPollRejectsMissingAdmin() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertError(() -> pollService.create(99L, createRequest(List.of("A", "B"))), ErrorCode.NOT_FOUND);
    }

    @Test
    void createPollRejectsInactiveAdmin() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, "Admin", RoleType.ADMIN, false)));

        assertError(() -> pollService.create(1L, createRequest(List.of("A", "B"))), ErrorCode.NOT_FOUND);
    }

    @Test
    void createPollRejectsLessThanTwoOptions() {
        stubAdmin();

        assertError(() -> pollService.create(1L, createRequest(List.of("A"))), ErrorCode.INVALID_POLL_REQUEST);
        verify(pollRepository, never()).save(any(Poll.class));
    }

    @Test
    void createPollRejectsMoreThanTenOptions() {
        stubAdmin();

        assertError(
                () -> pollService.create(1L, createRequest(List.of(
                        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"
                ))),
                ErrorCode.INVALID_POLL_REQUEST
        );
    }

    @Test
    void createPollRejectsDuplicateOptionsAfterTrim() {
        stubAdmin();

        assertError(() -> pollService.create(1L, createRequest(List.of("A", " A "))), ErrorCode.INVALID_POLL_REQUEST);
    }

    @Test
    void createPollRejectsBlankOption() {
        stubAdmin();

        assertError(() -> pollService.create(1L, createRequest(List.of("A", " "))), ErrorCode.INVALID_POLL_REQUEST);
    }

    @Test
    void createPollRejectsEndBeforeStart() {
        stubAdmin();

        assertError(
                () -> pollService.create(1L, createRequest(List.of("A", "B"), STARTS_AT, STARTS_AT.minusMinutes(1))),
                ErrorCode.INVALID_POLL_PERIOD
        );
    }

    @Test
    void createPollRejectsEqualStartAndEnd() {
        stubAdmin();

        assertError(
                () -> pollService.create(1L, createRequest(List.of("A", "B"), STARTS_AT, STARTS_AT)),
                ErrorCode.INVALID_POLL_PERIOD
        );
    }

    @Test
    void createPollRejectsPastEndAt() {
        stubAdmin();

        assertError(
                () -> pollService.create(
                        1L,
                        createRequest(
                                List.of("A", "B"),
                                LocalDateTime.of(2026, 6, 4, 9, 0),
                                LocalDateTime.of(2026, 6, 4, 18, 0)
                        )
                ),
                ErrorCode.INVALID_POLL_PERIOD
        );
    }

    @Test
    void getPollsReturnsPollsWithHasVoted() {
        Poll poll = poll(100L, PollStatus.OPEN);
        Pageable pageable = PageRequest.of(0, 10, org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Direction.DESC,
                "createdAt"
        ));
        when(pollRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(poll), pageable, 1));
        when(pollVoteRepository.existsByPollIdAndVoterId(100L, 2L)).thenReturn(true);

        PollPageResponse response = pollService.getPolls(null, 0, 10, 2L);

        assertThat(response.content()).singleElement().satisfies(pollResponse -> {
            assertThat(pollResponse.id()).isEqualTo(100L);
            assertThat(pollResponse.hasVoted()).isTrue();
        });
    }

    @Test
    void getPollsFiltersByStatus() {
        Pageable pageable = PageRequest.of(0, 10, org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Direction.DESC,
                "createdAt"
        ));
        when(pollRepository.findByStatus(PollStatus.OPEN, pageable)).thenReturn(new PageImpl<>(List.of(), pageable, 0));

        pollService.getPolls(PollStatus.OPEN, 0, 10, 2L);

        verify(pollRepository).findByStatus(PollStatus.OPEN, pageable);
    }

    @Test
    void getPollReturnsDetailWithOptionsAndHasVoted() {
        Poll poll = poll(100L, PollStatus.OPEN);
        PollOption first = option(1L, poll, "A", 1);
        PollOption second = option(2L, poll, "B", 2);
        when(pollRepository.findById(100L)).thenReturn(Optional.of(poll));
        when(pollOptionRepository.findByPollIdOrderByDisplayOrderAsc(100L)).thenReturn(List.of(first, second));
        when(pollVoteRepository.existsByPollIdAndVoterId(100L, 2L)).thenReturn(true);

        PollDetailResponse response = pollService.getPoll(100L, 2L);

        assertThat(response.options()).extracting("text").containsExactly("A", "B");
        assertThat(response.hasVoted()).isTrue();
    }

    @Test
    void getPollRejectsMissingPoll() {
        when(pollRepository.findById(999L)).thenReturn(Optional.empty());

        assertError(() -> pollService.getPoll(999L, 2L), ErrorCode.POLL_NOT_FOUND);
    }

    @Test
    void updatePollChangesProvidedFields() {
        Poll poll = poll(100L, PollStatus.OPEN);
        when(pollRepository.findById(100L)).thenReturn(Optional.of(poll));
        when(pollOptionRepository.findByPollIdOrderByDisplayOrderAsc(100L)).thenReturn(options(poll));

        PollDetailResponse response = pollService.update(
                100L,
                new UpdatePollRequest("Updated", null, true, ENDS_AT.plusDays(1))
        );

        assertThat(response.title()).isEqualTo("Updated");
        assertThat(response.description()).isEqualTo("Description");
        assertThat(response.resultVisible()).isTrue();
        assertThat(response.endsAt()).isEqualTo(ENDS_AT.plusDays(1));
        verify(pollRepository).flush();
    }

    @Test
    void updatePollRejectsEmptyRequest() {
        assertError(
                () -> pollService.update(100L, new UpdatePollRequest(null, null, null, null)),
                ErrorCode.INVALID_POLL_REQUEST
        );
        verify(pollRepository, never()).findById(100L);
    }

    @Test
    void updatePollRejectsBlankTitle() {
        assertError(
                () -> pollService.update(100L, new UpdatePollRequest(" ", null, null, null)),
                ErrorCode.INVALID_POLL_REQUEST
        );
    }

    @Test
    void updatePollRejectsClosedPoll() {
        when(pollRepository.findById(100L)).thenReturn(Optional.of(poll(100L, PollStatus.CLOSED)));

        assertError(
                () -> pollService.update(100L, new UpdatePollRequest("Updated", null, null, null)),
                ErrorCode.POLL_ALREADY_CLOSED
        );
    }

    @Test
    void updatePollRejectsInvalidEndAt() {
        when(pollRepository.findById(100L)).thenReturn(Optional.of(poll(100L, PollStatus.OPEN)));

        assertError(
                () -> pollService.update(100L, new UpdatePollRequest(null, null, null, STARTS_AT)),
                ErrorCode.INVALID_POLL_PERIOD
        );
    }

    @Test
    void closePoll() {
        Poll poll = poll(100L, PollStatus.OPEN);
        when(pollRepository.findById(100L)).thenReturn(Optional.of(poll));
        when(pollOptionRepository.findByPollIdOrderByDisplayOrderAsc(100L)).thenReturn(options(poll));

        PollDetailResponse response = pollService.close(100L);

        assertThat(response.status()).isEqualTo(PollStatus.CLOSED);
        verify(pollRepository).flush();
    }

    @Test
    void closePollRejectsAlreadyClosedPoll() {
        when(pollRepository.findById(100L)).thenReturn(Optional.of(poll(100L, PollStatus.CLOSED)));

        assertError(() -> pollService.close(100L), ErrorCode.POLL_ALREADY_CLOSED);
        verify(pollRepository, never()).flush();
    }

    @Test
    void paginationRejectsNegativePage() {
        assertError(() -> pollService.getPolls(null, -1, 10, 2L), ErrorCode.INVALID_POLL_REQUEST);
    }

    @Test
    void paginationRejectsZeroSize() {
        assertError(() -> pollService.getPolls(null, 0, 0, 2L), ErrorCode.INVALID_POLL_REQUEST);
    }

    @Test
    void paginationLimitsSizeToFifty() {
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(pollRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        pollService.getPolls(null, 0, 100, 2L);

        verify(pollRepository).findAll(pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(50);
    }

    @Test
    void studentVotesPoll() {
        User student = user(2L, "Student", RoleType.STUDENT, true);
        Poll poll = poll(100L, PollStatus.OPEN);
        PollOption option = option(10L, poll, "A", 1);
        when(userRepository.findById(2L)).thenReturn(Optional.of(student));
        when(pollRepository.findById(100L)).thenReturn(Optional.of(poll));
        when(pollOptionRepository.findById(10L)).thenReturn(Optional.of(option));
        when(pollVoteRepository.existsByPollIdAndVoterId(100L, 2L)).thenReturn(false);
        when(pollVoteRepository.save(any(PollVote.class))).thenAnswer(invocation -> {
            PollVote vote = invocation.getArgument(0);
            ReflectionTestUtils.setField(vote, "id", 1L);
            ReflectionTestUtils.setField(vote, "createdAt", STARTS_AT.plusMinutes(1));
            return vote;
        });

        PollVoteResponse response = pollService.vote(2L, 100L, new VotePollRequest(10L));

        assertThat(response.pollId()).isEqualTo(100L);
        assertThat(response.optionId()).isEqualTo(10L);
        assertThat(response.votedAt()).isEqualTo(STARTS_AT.plusMinutes(1));
    }

    @Test
    void voteRejectsAdmin() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin()));

        assertError(() -> pollService.vote(1L, 100L, new VotePollRequest(10L)), ErrorCode.FORBIDDEN);
        verify(pollRepository, never()).findById(100L);
    }

    @Test
    void voteRejectsMissingVoter() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertError(() -> pollService.vote(99L, 100L, new VotePollRequest(10L)), ErrorCode.NOT_FOUND);
    }

    @Test
    void voteRejectsMissingPoll() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2L, "Student", RoleType.STUDENT, true)));
        when(pollRepository.findById(999L)).thenReturn(Optional.empty());

        assertError(() -> pollService.vote(2L, 999L, new VotePollRequest(10L)), ErrorCode.POLL_NOT_FOUND);
    }

    @Test
    void voteRejectsMissingOption() {
        Poll poll = poll(100L, PollStatus.OPEN);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2L, "Student", RoleType.STUDENT, true)));
        when(pollRepository.findById(100L)).thenReturn(Optional.of(poll));
        when(pollOptionRepository.findById(99L)).thenReturn(Optional.empty());

        assertError(() -> pollService.vote(2L, 100L, new VotePollRequest(99L)), ErrorCode.POLL_OPTION_NOT_FOUND);
    }

    @Test
    void voteRejectsOptionFromOtherPoll() {
        Poll poll = poll(100L, PollStatus.OPEN);
        Poll otherPoll = poll(200L, PollStatus.OPEN);
        PollOption option = option(10L, otherPoll, "A", 1);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2L, "Student", RoleType.STUDENT, true)));
        when(pollRepository.findById(100L)).thenReturn(Optional.of(poll));
        when(pollOptionRepository.findById(10L)).thenReturn(Optional.of(option));

        assertError(() -> pollService.vote(2L, 100L, new VotePollRequest(10L)), ErrorCode.POLL_OPTION_MISMATCH);
    }

    @Test
    void voteRejectsBeforeStart() {
        Poll poll = poll(100L, PollStatus.OPEN);
        ReflectionTestUtils.setField(poll, "startsAt", LocalDateTime.of(2026, 6, 6, 9, 0));
        ReflectionTestUtils.setField(poll, "endsAt", LocalDateTime.of(2026, 6, 7, 9, 0));
        stubStudentPollAndOption(poll, option(10L, poll, "A", 1));

        assertError(() -> pollService.vote(2L, 100L, new VotePollRequest(10L)), ErrorCode.POLL_NOT_STARTED);
    }

    @Test
    void voteRejectsAfterEnd() {
        Poll poll = poll(100L, PollStatus.OPEN);
        ReflectionTestUtils.setField(poll, "startsAt", LocalDateTime.of(2026, 6, 4, 9, 0));
        ReflectionTestUtils.setField(poll, "endsAt", LocalDateTime.of(2026, 6, 5, 9, 0));
        stubStudentPollAndOption(poll, option(10L, poll, "A", 1));

        assertError(() -> pollService.vote(2L, 100L, new VotePollRequest(10L)), ErrorCode.POLL_ENDED);
    }

    @Test
    void voteRejectsClosedPoll() {
        Poll poll = poll(100L, PollStatus.CLOSED);
        stubStudentPollAndOption(poll, option(10L, poll, "A", 1));

        assertError(() -> pollService.vote(2L, 100L, new VotePollRequest(10L)), ErrorCode.POLL_NOT_OPEN);
    }

    @Test
    void voteRejectsAlreadyVoted() {
        Poll poll = poll(100L, PollStatus.OPEN);
        PollOption option = option(10L, poll, "A", 1);
        stubStudentPollAndOption(poll, option);
        when(pollVoteRepository.existsByPollIdAndVoterId(100L, 2L)).thenReturn(true);

        assertError(() -> pollService.vote(2L, 100L, new VotePollRequest(10L)), ErrorCode.POLL_ALREADY_VOTED);
        verify(pollVoteRepository, never()).save(any(PollVote.class));
    }

    @Test
    void voteTreatsUniqueConstraintConflictAsAlreadyVoted() {
        Poll poll = poll(100L, PollStatus.OPEN);
        PollOption option = option(10L, poll, "A", 1);
        stubStudentPollAndOption(poll, option);
        when(pollVoteRepository.existsByPollIdAndVoterId(100L, 2L)).thenReturn(false);
        when(pollVoteRepository.save(any(PollVote.class)))
                .thenThrow(new DataIntegrityViolationException("uq_poll_votes_poll_voter"));

        assertError(() -> pollService.vote(2L, 100L, new VotePollRequest(10L)), ErrorCode.POLL_ALREADY_VOTED);
    }

    @Test
    void adminGetsResult() {
        Poll poll = poll(100L, PollStatus.OPEN);
        when(pollRepository.findById(100L)).thenReturn(Optional.of(poll));
        when(pollOptionRepository.findByPollIdOrderByDisplayOrderAsc(100L)).thenReturn(options(poll));
        when(pollVoteRepository.countByPollId(100L)).thenReturn(3L);
        when(pollVoteRepository.countByOptionId(1L)).thenReturn(2L);
        when(pollVoteRepository.countByOptionId(2L)).thenReturn(1L);

        PollResultResponse response = pollService.getResult(1L, RoleType.ADMIN, 100L);

        assertThat(response.totalVotes()).isEqualTo(3L);
        assertThat(response.options()).extracting("voteCount").containsExactly(2L, 1L);
        assertThat(response.options().get(0).percentage()).isCloseTo(66.666, org.assertj.core.data.Offset.offset(0.01));
    }

    @Test
    void studentCannotGetResultWhenResultInvisible() {
        Poll poll = poll(100L, PollStatus.OPEN);
        ReflectionTestUtils.setField(poll, "resultVisible", false);
        when(pollRepository.findById(100L)).thenReturn(Optional.of(poll));
        when(pollVoteRepository.existsByPollIdAndVoterId(100L, 2L)).thenReturn(true);

        assertError(() -> pollService.getResult(2L, RoleType.STUDENT, 100L), ErrorCode.POLL_RESULT_ACCESS_DENIED);
    }

    @Test
    void studentCannotGetVisibleResultWhenNotVotedAndPollIsRunning() {
        Poll poll = poll(100L, PollStatus.OPEN);
        ReflectionTestUtils.setField(poll, "resultVisible", true);
        when(pollRepository.findById(100L)).thenReturn(Optional.of(poll));
        when(pollVoteRepository.existsByPollIdAndVoterId(100L, 2L)).thenReturn(false);

        assertError(() -> pollService.getResult(2L, RoleType.STUDENT, 100L), ErrorCode.POLL_RESULT_ACCESS_DENIED);
    }

    @Test
    void studentGetsVisibleResultAfterVoting() {
        Poll poll = poll(100L, PollStatus.OPEN);
        ReflectionTestUtils.setField(poll, "resultVisible", true);
        when(pollRepository.findById(100L)).thenReturn(Optional.of(poll));
        when(pollVoteRepository.existsByPollIdAndVoterId(100L, 2L)).thenReturn(true);
        when(pollOptionRepository.findByPollIdOrderByDisplayOrderAsc(100L)).thenReturn(options(poll));
        when(pollVoteRepository.countByPollId(100L)).thenReturn(1L);
        when(pollVoteRepository.countByOptionId(1L)).thenReturn(1L);
        when(pollVoteRepository.countByOptionId(2L)).thenReturn(0L);

        PollResultResponse response = pollService.getResult(2L, RoleType.STUDENT, 100L);

        assertThat(response.totalVotes()).isEqualTo(1L);
    }

    @Test
    void studentGetsVisibleResultWhenPollIsClosed() {
        Poll poll = poll(100L, PollStatus.CLOSED);
        ReflectionTestUtils.setField(poll, "resultVisible", true);
        when(pollRepository.findById(100L)).thenReturn(Optional.of(poll));
        when(pollVoteRepository.existsByPollIdAndVoterId(100L, 2L)).thenReturn(false);
        when(pollOptionRepository.findByPollIdOrderByDisplayOrderAsc(100L)).thenReturn(options(poll));
        when(pollVoteRepository.countByPollId(100L)).thenReturn(0L);

        PollResultResponse response = pollService.getResult(2L, RoleType.STUDENT, 100L);

        assertThat(response.status()).isEqualTo(PollStatus.CLOSED);
    }

    @Test
    void studentGetsVisibleResultWhenPollHasEnded() {
        Poll poll = poll(100L, PollStatus.OPEN);
        ReflectionTestUtils.setField(poll, "resultVisible", true);
        ReflectionTestUtils.setField(poll, "endsAt", LocalDateTime.of(2026, 6, 5, 9, 0));
        when(pollRepository.findById(100L)).thenReturn(Optional.of(poll));
        when(pollVoteRepository.existsByPollIdAndVoterId(100L, 2L)).thenReturn(false);
        when(pollOptionRepository.findByPollIdOrderByDisplayOrderAsc(100L)).thenReturn(options(poll));
        when(pollVoteRepository.countByPollId(100L)).thenReturn(0L);

        PollResultResponse response = pollService.getResult(2L, RoleType.STUDENT, 100L);

        assertThat(response.pollId()).isEqualTo(100L);
    }

    @Test
    void resultReturnsZeroPercentagesWhenNoVotesExist() {
        Poll poll = poll(100L, PollStatus.OPEN);
        when(pollRepository.findById(100L)).thenReturn(Optional.of(poll));
        when(pollOptionRepository.findByPollIdOrderByDisplayOrderAsc(100L)).thenReturn(options(poll));
        when(pollVoteRepository.countByPollId(100L)).thenReturn(0L);

        PollResultResponse response = pollService.getResult(1L, RoleType.ADMIN, 100L);

        assertThat(response.options()).extracting("percentage").containsExactly(0.0, 0.0);
    }

    @Test
    void resultDoesNotExposeVoterInformation() {
        assertThat(PollResultResponse.class.getRecordComponents())
                .extracting(java.lang.reflect.RecordComponent::getName)
                .doesNotContain("voter", "voterId", "voters");
        assertThat(PollResultOptionResponse.class.getRecordComponents())
                .extracting(java.lang.reflect.RecordComponent::getName)
                .doesNotContain("voter", "voterId", "voters");
    }

    private void stubAdmin() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin()));
    }

    private CreatePollRequest createRequest(List<String> options) {
        return createRequest(options, STARTS_AT, ENDS_AT);
    }

    private CreatePollRequest createRequest(
            List<String> options,
            LocalDateTime startsAt,
            LocalDateTime endsAt
    ) {
        return new CreatePollRequest(
                "Poll",
                "Description",
                null,
                null,
                startsAt,
                endsAt,
                options
        );
    }

    private User admin() {
        return user(1L, "Admin", RoleType.ADMIN, true);
    }

    private void stubStudentPollAndOption(Poll poll, PollOption option) {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2L, "Student", RoleType.STUDENT, true)));
        when(pollRepository.findById(100L)).thenReturn(Optional.of(poll));
        when(pollOptionRepository.findById(10L)).thenReturn(Optional.of(option));
    }

    private User user(Long id, String name, RoleType role, boolean active) {
        User user = new User(name.toLowerCase() + "@lgb.local", "encoded", name, role);
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "active", active);
        return user;
    }

    private Poll poll(Long id, PollStatus status) {
        Poll poll = new Poll(
                "Poll",
                "Description",
                true,
                false,
                STARTS_AT,
                ENDS_AT,
                admin()
        );
        setPollFields(poll, id, status);
        return poll;
    }

    private void setPollFields(Poll poll, Long id, PollStatus status) {
        ReflectionTestUtils.setField(poll, "id", id);
        ReflectionTestUtils.setField(poll, "status", status);
        ReflectionTestUtils.setField(poll, "createdAt", STARTS_AT.minusDays(1));
        ReflectionTestUtils.setField(poll, "updatedAt", STARTS_AT.minusDays(1));
    }

    private List<PollOption> options(Poll poll) {
        return List.of(
                option(1L, poll, "A", 1),
                option(2L, poll, "B", 2)
        );
    }

    private PollOption option(Long id, Poll poll, String text, int displayOrder) {
        PollOption option = new PollOption(poll, text, displayOrder);
        ReflectionTestUtils.setField(option, "id", id);
        ReflectionTestUtils.setField(option, "createdAt", STARTS_AT.minusDays(1));
        ReflectionTestUtils.setField(option, "updatedAt", STARTS_AT.minusDays(1));
        return option;
    }

    private void assertError(Runnable action, ErrorCode errorCode) {
        assertThatThrownBy(action::run)
                .isInstanceOf(CustomException.class)
                .hasMessage(errorCode.getMessage());
    }
}
