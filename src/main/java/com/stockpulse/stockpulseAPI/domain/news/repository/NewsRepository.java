package com.stockpulse.stockpulseAPI.domain.news.repository;

import com.stockpulse.stockpulseAPI.domain.news.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long>, NewsRepositoryCustom {
    boolean existsByUrl(String url);

    Optional<News> findByUrl(String url);

    @Query("SELECT n FROM News n LEFT JOIN FETCH n.impacts WHERE n.id = :id")
    Optional<News> findByIdWithImpacts(@Param("id") Long id);
}
