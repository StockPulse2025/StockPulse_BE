package com.stockpulse.stockpulseAPI.domain.news.repository;

import com.stockpulse.stockpulseAPI.domain.news.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    boolean existsByUrl(String url);

    Optional<News> findByUrl(String url);
}
