package com.stockpulse.stockpulseAPI.domain.post.repository;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.post.entity.Post;
import com.stockpulse.stockpulseAPI.domain.post.entity.Vote;
import com.stockpulse.stockpulseAPI.domain.post.entity.enums.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    public Optional<Vote> findByPost(Post post);
}
