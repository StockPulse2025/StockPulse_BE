package com.stockpulse.stockpulseAPI.domain.news.repository;

import com.stockpulse.stockpulseAPI.domain.news.entity.NewsImpact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NewsImpactRepository extends JpaRepository<NewsImpact, Long> {

    @Query("SELECT n FROM Notification n WHERE n.updatedAt >= :tenMinutesAgo")
    List<NewsImpact> findRecentlyUpdated(@Param("tenMinutesAgo") LocalDateTime tenMinutesAgo);
    List<NewsImpact> findAll();
}
