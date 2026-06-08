package com.LGB.domain.poll.repository;

import com.LGB.domain.poll.entity.PollOption;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollOptionRepository extends JpaRepository<PollOption, Long> {

    List<PollOption> findByPollIdOrderByDisplayOrderAsc(Long pollId);

    Optional<PollOption> findByIdAndPollId(Long optionId, Long pollId);

    boolean existsByIdAndPollId(Long optionId, Long pollId);
}
