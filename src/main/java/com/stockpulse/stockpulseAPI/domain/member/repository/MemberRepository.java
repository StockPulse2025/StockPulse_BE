package com.stockpulse.stockpulseAPI.domain.member.repository;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.news.entity.MemberScrapNews;
import com.stockpulse.stockpulseAPI.domain.news.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    @Query("""
            SELECT msn 
            FROM MemberScrapNews msn
            JOIN FETCH msn.news n           
            WHERE msn.member.id = :userId  
            ORDER BY msn.createdAt DESC            
            """)
    List<MemberScrapNews> findScrappedNewsWithNewsByUserId(@Param("userId") Long userId);
}