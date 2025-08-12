package com.stockpulse.stockpulseAPI.domain.post.repository;

import com.stockpulse.stockpulseAPI.domain.post.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
