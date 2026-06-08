package com.LGB.domain.poll.service;

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
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PollService {

    private static final int MIN_OPTION_COUNT = 2;
    private static final int MAX_OPTION_COUNT = 10;
    private static final int MAX_PAGE_SIZE = 50;

    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;
    private final PollVoteRepository pollVoteRepository;
    private final UserRepository userRepository;
    private final Clock clock;

    @Autowired
    public PollService(
            PollRepository pollRepository,
            PollOptionRepository pollOptionRepository,
            PollVoteRepository pollVoteRepository,
            UserRepository userRepository
    ) {
        this(
                pollRepository,
                pollOptionRepository,
                pollVoteRepository,
                userRepository,
                Clock.systemDefaultZone()
        );
    }

    PollService(
            PollRepository pollRepository,
            PollOptionRepository pollOptionRepository,
            PollVoteRepository pollVoteRepository,
            UserRepository userRepository,
            Clock clock
    ) {
        this.pollRepository = pollRepository;
        this.pollOptionRepository = pollOptionRepository;
        this.pollVoteRepository = pollVoteRepository;
        this.userRepository = userRepository;
        this.clock = clock;
    }

    @Transactional
    public PollDetailResponse create(Long adminId, CreatePollRequest request) {
        User admin = findActiveAdmin(adminId);
        validateCreateRequest(request);
        validateCreatePeriod(request.startsAt(), request.endsAt());

        Poll poll = new Poll(
                request.title(),
                request.description(),
                request.anonymous() == null || request.anonymous(),
                Boolean.TRUE.equals(request.resultVisible()),
                request.startsAt(),
                request.endsAt(),
                admin
        );
        Poll savedPoll = pollRepository.save(poll);

        List<PollOption> options = createOptions(savedPoll, request.options());
        pollOptionRepository.saveAll(options);

        return PollDetailResponse.from(savedPoll, options, false);
    }

    public PollPageResponse getPolls(
            PollStatus status,
            int page,
            int size,
            Long currentUserId
    ) {
        int limitedSize = validateAndLimitPage(page, size);
        PageRequest pageable = PageRequest.of(
                page,
                limitedSize,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<Poll> polls = status == null
                ? pollRepository.findAll(pageable)
                : pollRepository.findByStatus(status, pageable);

        return PollPageResponse.from(polls.map(
                poll -> com.LGB.domain.poll.dto.PollListResponse.from(
                        poll,
                        hasVoted(poll.getId(), currentUserId)
                )
        ));
    }

    public PollDetailResponse getPoll(Long pollId, Long currentUserId) {
        Poll poll = findPoll(pollId);
        List<PollOption> options = pollOptionRepository.findByPollIdOrderByDisplayOrderAsc(pollId);

        return PollDetailResponse.from(poll, options, hasVoted(pollId, currentUserId));
    }

    @Transactional
    public PollDetailResponse update(Long pollId, UpdatePollRequest request) {
        validateUpdateRequest(request);

        Poll poll = findPoll(pollId);
        if (poll.getStatus() == PollStatus.CLOSED) {
            throw new CustomException(ErrorCode.POLL_ALREADY_CLOSED);
        }

        LocalDateTime finalEndsAt = request.endsAt() == null ? poll.getEndsAt() : request.endsAt();
        validateEndAfterStart(poll.getStartsAt(), finalEndsAt);

        poll.update(request.title(), request.description(), request.resultVisible(), request.endsAt());
        pollRepository.flush();

        List<PollOption> options = pollOptionRepository.findByPollIdOrderByDisplayOrderAsc(pollId);
        return PollDetailResponse.from(poll, options, false);
    }

    @Transactional
    public PollDetailResponse close(Long pollId) {
        Poll poll = findPoll(pollId);
        if (poll.getStatus() == PollStatus.CLOSED) {
            throw new CustomException(ErrorCode.POLL_ALREADY_CLOSED);
        }

        poll.close();
        pollRepository.flush();

        List<PollOption> options = pollOptionRepository.findByPollIdOrderByDisplayOrderAsc(pollId);
        return PollDetailResponse.from(poll, options, false);
    }

    @Transactional
    public PollVoteResponse vote(Long voterId, Long pollId, VotePollRequest request) {
        User voter = findActiveStudent(voterId);
        Poll poll = findPoll(pollId);
        PollOption option = findVoteOption(request, pollId);

        validateVotePeriod(poll);
        if (pollVoteRepository.existsByPollIdAndVoterId(pollId, voterId)) {
            throw new CustomException(ErrorCode.POLL_ALREADY_VOTED);
        }

        try {
            PollVote vote = pollVoteRepository.save(new PollVote(poll, option, voter));
            return new PollVoteResponse(poll.getId(), option.getId(), vote.getCreatedAt());
        } catch (DataIntegrityViolationException exception) {
            throw new CustomException(ErrorCode.POLL_ALREADY_VOTED);
        }
    }

    public PollResultResponse getResult(Long currentUserId, RoleType role, Long pollId) {
        Poll poll = findPoll(pollId);
        LocalDateTime now = LocalDateTime.now(clock);
        boolean currentUserVoted = hasVoted(pollId, currentUserId);

        if (role != RoleType.ADMIN && !canStudentAccessResult(poll, currentUserVoted, now)) {
            throw new CustomException(ErrorCode.POLL_RESULT_ACCESS_DENIED);
        }

        List<PollOption> options = pollOptionRepository.findByPollIdOrderByDisplayOrderAsc(pollId);
        long totalVotes = pollVoteRepository.countByPollId(pollId);
        List<PollResultOptionResponse> resultOptions = options.stream()
                .map(option -> resultOption(option, totalVotes))
                .toList();

        return new PollResultResponse(
                poll.getId(),
                poll.getTitle(),
                totalVotes,
                resultOptions,
                poll.isAnonymous(),
                poll.isResultVisible(),
                poll.getStatus(),
                poll.getStartsAt(),
                poll.getEndsAt()
        );
    }

    private User findActiveAdmin(Long adminId) {
        User admin = userRepository.findById(adminId)
                .filter(User::isActive)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        if (admin.getRole() != RoleType.ADMIN) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        return admin;
    }

    private User findActiveStudent(Long voterId) {
        User voter = userRepository.findById(voterId)
                .filter(User::isActive)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        if (voter.getRole() != RoleType.STUDENT) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        return voter;
    }

    private Poll findPoll(Long pollId) {
        return pollRepository.findById(pollId)
                .orElseThrow(() -> new CustomException(ErrorCode.POLL_NOT_FOUND));
    }

    private PollOption findVoteOption(VotePollRequest request, Long pollId) {
        if (request == null || request.optionId() == null) {
            throw new CustomException(ErrorCode.INVALID_POLL_REQUEST);
        }

        PollOption option = pollOptionRepository.findById(request.optionId())
                .orElseThrow(() -> new CustomException(ErrorCode.POLL_OPTION_NOT_FOUND));
        if (!option.getPoll().getId().equals(pollId)) {
            throw new CustomException(ErrorCode.POLL_OPTION_MISMATCH);
        }
        return option;
    }

    private void validateCreateRequest(CreatePollRequest request) {
        if (request == null) {
            throw new CustomException(ErrorCode.INVALID_POLL_REQUEST);
        }
        validateOptions(request.options());
    }

    private void validateOptions(List<String> options) {
        if (options == null || options.size() < MIN_OPTION_COUNT || options.size() > MAX_OPTION_COUNT) {
            throw new CustomException(ErrorCode.INVALID_POLL_REQUEST);
        }

        Set<String> normalizedOptions = new HashSet<>();
        for (String option : options) {
            if (option == null || option.isBlank()) {
                throw new CustomException(ErrorCode.INVALID_POLL_REQUEST);
            }
            if (!normalizedOptions.add(option.trim())) {
                throw new CustomException(ErrorCode.INVALID_POLL_REQUEST);
            }
        }
    }

    private void validateCreatePeriod(LocalDateTime startsAt, LocalDateTime endsAt) {
        validateEndAfterStart(startsAt, endsAt);
        if (!endsAt.isAfter(LocalDateTime.now(clock))) {
            throw new CustomException(ErrorCode.INVALID_POLL_PERIOD);
        }
    }

    private void validateEndAfterStart(LocalDateTime startsAt, LocalDateTime endsAt) {
        if (startsAt == null || endsAt == null || !endsAt.isAfter(startsAt)) {
            throw new CustomException(ErrorCode.INVALID_POLL_PERIOD);
        }
    }

    private void validateUpdateRequest(UpdatePollRequest request) {
        if (request == null
                || (request.title() == null
                && request.description() == null
                && request.resultVisible() == null
                && request.endsAt() == null)) {
            throw new CustomException(ErrorCode.INVALID_POLL_REQUEST);
        }
        if (request.title() != null && request.title().isBlank()) {
            throw new CustomException(ErrorCode.INVALID_POLL_REQUEST);
        }
    }

    private int validateAndLimitPage(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new CustomException(ErrorCode.INVALID_POLL_REQUEST);
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }

    private List<PollOption> createOptions(Poll poll, List<String> optionTexts) {
        return java.util.stream.IntStream.range(0, optionTexts.size())
                .mapToObj(index -> new PollOption(poll, optionTexts.get(index), index + 1))
                .toList();
    }

    private boolean hasVoted(Long pollId, Long currentUserId) {
        return currentUserId != null
                && pollVoteRepository.existsByPollIdAndVoterId(pollId, currentUserId);
    }

    private void validateVotePeriod(Poll poll) {
        LocalDateTime now = LocalDateTime.now(clock);
        if (poll.getStatus() == PollStatus.CLOSED) {
            throw new CustomException(ErrorCode.POLL_NOT_OPEN);
        }
        if (now.isBefore(poll.getStartsAt())) {
            throw new CustomException(ErrorCode.POLL_NOT_STARTED);
        }
        if (!now.isBefore(poll.getEndsAt())) {
            throw new CustomException(ErrorCode.POLL_ENDED);
        }
    }

    private boolean canStudentAccessResult(Poll poll, boolean currentUserVoted, LocalDateTime now) {
        return poll.isResultVisible()
                && (currentUserVoted
                || poll.getStatus() == PollStatus.CLOSED
                || !now.isBefore(poll.getEndsAt()));
    }

    private PollResultOptionResponse resultOption(PollOption option, long totalVotes) {
        long voteCount = pollVoteRepository.countByOptionId(option.getId());
        double percentage = totalVotes == 0L ? 0.0 : (double) voteCount * 100.0 / totalVotes;

        return new PollResultOptionResponse(
                option.getId(),
                option.getText(),
                option.getDisplayOrder(),
                voteCount,
                percentage
        );
    }
}
