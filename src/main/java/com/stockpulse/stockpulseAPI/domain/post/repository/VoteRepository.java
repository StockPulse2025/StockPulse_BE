package com.stockpulse.stockpulseAPI.domain.post.repository;

import com.stockpulse.stockpulseAPI.domain.post.entity.Post;
import com.stockpulse.stockpulseAPI.domain.post.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByPost(Post post);
}
