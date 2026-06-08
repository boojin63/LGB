package com.LGB.domain.poll.repository;

import com.LGB.domain.poll.entity.Poll;
import com.LGB.domain.poll.entity.PollStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollRepository extends JpaRepository<Poll, Long> {

    Page<Poll> findByStatus(PollStatus status, Pageable pageable);
}
