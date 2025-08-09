package com.stockpulse.stockpulseAPI.domain.news.repository;

import com.stockpulse.stockpulseAPI.domain.news.entity.Impact;
import com.stockpulse.stockpulseAPI.domain.news.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ImpactRepository extends JpaRepository<Impact, Long> {

    @Query("SELECT n FROM Notification n WHERE n.updatedAt >= :tenMinutesAgo")
    List<Impact> findRecentlyUpdated(@Param("tenMinutesAgo") LocalDateTime tenMinutesAgo);
    List<Impact> findAll();
}
