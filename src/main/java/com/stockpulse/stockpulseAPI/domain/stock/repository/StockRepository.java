package com.stockpulse.stockpulseAPI.domain.stock.repository;

import com.stockpulse.stockpulseAPI.domain.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findBySymbol(String symbol);

    @Query("SELECT s FROM Stock s WHERE " +
            "LOWER(s.symbol) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Stock> searchStocks(@Param("keyword") String keyword);
}
