package com.stockpulse.stockpulseAPI.domain.stock.repository;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.stock.entity.MemberOwnStock;
import com.stockpulse.stockpulseAPI.domain.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberOwnStockRepository extends JpaRepository<MemberOwnStock, Long> {

    @Query("SELECT mos FROM MemberOwnStock mos WHERE mos.member = :member AND mos.stock = :stock")
    Optional<MemberOwnStock> findByMemberAndStock(@Param("member") Member member, @Param("stock") Stock stock);
    
    boolean existsByMemberAndStock(Member member, Stock stock);
}
