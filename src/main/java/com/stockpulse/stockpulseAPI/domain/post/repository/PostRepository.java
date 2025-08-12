package com.stockpulse.stockpulseAPI.domain.post.repository;

import com.stockpulse.stockpulseAPI.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
