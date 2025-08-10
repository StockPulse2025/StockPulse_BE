package com.stockpulse.stockpulseAPI.domain.stock.repository;

import com.stockpulse.stockpulseAPI.domain.stock.entity.MemberOwnStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberOwnStockRepository extends JpaRepository<MemberOwnStock, Long> {
}
