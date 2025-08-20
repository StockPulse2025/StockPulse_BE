package com.stockpulse.stockpulseAPI.domain.stock.repository;

import com.stockpulse.stockpulseAPI.domain.stock.entity.Stock;
import com.stockpulse.stockpulseAPI.domain.stock.entity.StockTick;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockTickRepository extends JpaRepository<StockTick, Long> {
    Optional<StockTick> findByStock(Stock stock);
}
