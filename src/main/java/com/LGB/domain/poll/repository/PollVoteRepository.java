package com.LGB.domain.poll.repository;

import com.LGB.domain.poll.entity.PollVote;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollVoteRepository extends JpaRepository<PollVote, Long> {

    boolean existsByPollIdAndVoterId(Long pollId, Long voterId);

    Optional<PollVote> findByPollIdAndVoterId(Long pollId, Long voterId);

    long countByPollId(Long pollId);

    long countByOptionId(Long optionId);
}
