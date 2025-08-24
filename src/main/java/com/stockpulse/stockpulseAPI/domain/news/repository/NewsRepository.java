package com.stockpulse.stockpulseAPI.domain.news.repository;

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
public interface NewsRepository extends JpaRepository<News, Long>, NewsRepositoryCustom {
    boolean existsByUrl(String url);

    Optional<News> findByUrl(String url);

    @Query("SELECT n FROM News n LEFT JOIN FETCH n.impacts WHERE n.id = :id")
    Optional<News> findByIdWithImpacts(@Param("id") Long id);

    @Query("SELECT n FROM News n " +
            "JOIN n.impacts i " +
            "WHERE i.stock.id = :stockId " +
            "AND n.publishedDate BETWEEN :startDate AND :endDate " +
            "GROUP BY n " +
            "ORDER BY MAX(ABS(i.impactRate)) DESC")
    List<News> findByStockAndDateRange(@Param("stockId") Long stockId, 
                                       @Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DISTINCT n FROM News n " +
            "JOIN FETCH n.impacts i " +
            "WHERE i.stock IN :stocks " +
            "ORDER BY n.publishedDate DESC")
    List<News> findLatestNewsByStocks(@Param("stocks") List<Stock> stocks, Pageable pageable);

}
