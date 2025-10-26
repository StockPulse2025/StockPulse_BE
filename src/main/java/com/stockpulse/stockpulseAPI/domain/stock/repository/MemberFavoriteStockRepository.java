package com.stockpulse.stockpulseAPI.domain.stock.repository;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.stock.entity.Stock;
import com.stockpulse.stockpulseAPI.domain.stock.entity.MemberFavoriteStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberFavoriteStockRepository extends JpaRepository<MemberFavoriteStock, Long> {

    @Query("SELECT ufs.member FROM MemberFavoriteStock ufs WHERE ufs.stock = :stock")
    Optional<List<Member>> findMembersByStock(Stock stock);

    @Query("SELECT mfs FROM MemberFavoriteStock mfs WHERE mfs.member = :member AND mfs.stock = :stock")
    Optional<MemberFavoriteStock> findByMemberAndStock(@Param("member") Member member, @Param("stock") Stock stock);
    
    boolean existsByMemberAndStock(Member member, Stock stock);

    @Query("""
      SELECT m FROM Member m
      JOIN MemberFavoriteStock mfs ON m.id = mfs.member.id
      JOIN MemberOwnStock mos ON m.id = mos.member.id
      WHERE mos.stock = :stock OR mfs.stock = :stock
    """)
    Optional<List<Member>> findMembersOwnStockOrMemberFavoriteStockByStockId(@Param("stock") Stock stock);

    @Query("SELECT mfs.stock FROM MemberFavoriteStock mfs WHERE mfs.member = :member")
    List<Stock> findStocksByMember(@Param("member") Member member);

    List<MemberFavoriteStock> findByMemberId(Long memberId);
}
