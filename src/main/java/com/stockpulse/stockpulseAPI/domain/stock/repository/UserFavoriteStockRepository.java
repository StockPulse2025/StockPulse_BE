package com.stockpulse.stockpulseAPI.domain.stock.repository;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.stock.entity.Stock;
import com.stockpulse.stockpulseAPI.domain.stock.entity.MemberFavoriteStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFavoriteStockRepository extends JpaRepository<MemberFavoriteStock, Long> {

    @Query("SELECT ufs.member FROM MemberFavoriteStock ufs WHERE ufs.stock = :stock")
    Optional<List<Member>> findMembersByStock(Stock stock);
}
