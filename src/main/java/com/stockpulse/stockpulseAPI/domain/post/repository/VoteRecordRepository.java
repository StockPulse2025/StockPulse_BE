package com.stockpulse.stockpulseAPI.domain.post.repository;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.post.entity.Vote;
import com.stockpulse.stockpulseAPI.domain.post.entity.VoteRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRecordRepository extends JpaRepository<VoteRecord, Long> {

    boolean existsByVoteAndMember(Vote vote, Member member);

    Optional<VoteRecord> findByVoteAndMember(Vote vote, Member member);
}
