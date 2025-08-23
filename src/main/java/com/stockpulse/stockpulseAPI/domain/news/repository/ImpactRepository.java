package com.stockpulse.stockpulseAPI.domain.news.repository;

import com.stockpulse.stockpulseAPI.domain.news.entity.Impact;
import com.stockpulse.stockpulseAPI.domain.news.entity.News;
import com.stockpulse.stockpulseAPI.domain.stock.entity.Stock;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ImpactRepository extends JpaRepository<Impact, Long> {

    @Query("SELECT n FROM Notification n WHERE n.updatedAt >= :tenMinutesAgo")
    List<Impact> findRecentlyUpdated(@Param("tenMinutesAgo") LocalDateTime tenMinutesAgo);
    List<Impact> findAll();

    @Query("SELECT i FROM Impact i JOIN FETCH i.news WHERE i.createdAt >= :todayStart ORDER BY ABS(i.impactRate) DESC")
    List<Impact> findTopByCreatedAtAfterOrderByImpactRateDescWithNews(@Param("todayStart") LocalDateTime todayStart, Pageable pageable);

    @Query("""
        SELECT i FROM Impact i
        JOIN FETCH i.stock
        WHERE i.news IN :newsList
        AND i.impactRate = (
            SELECT MAX(sub_i.impactRate)
            FROM Impact sub_i
            WHERE sub_i.news = i.news
        )
        """)
    List<Impact> findTopImpactsForNewsList(@Param("newsList") List<News> newsList);

    List<Impact> findByNews(News news);
    
    Optional<Impact> findByNewsAndStock(News news, Stock stock);
}