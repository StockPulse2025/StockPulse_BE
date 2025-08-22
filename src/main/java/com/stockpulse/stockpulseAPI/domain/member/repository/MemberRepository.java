package com.stockpulse.stockpulseAPI.domain.member.repository;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.news.entity.MemberScrapNews;
import com.stockpulse.stockpulseAPI.domain.post.entity.Comment;
import com.stockpulse.stockpulseAPI.domain.post.entity.Post;
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

    /**
     * 특정 사용자가 작성한 모든 게시글을 연관 엔티티(Member, News, Stock)와 함께 조회합니다.
     * JOIN FETCH를 사용하여 N+1 쿼리 문제를 방지합니다.
     * @param userId 사용자의 ID
     * @return 게시글 리스트
     */
    @Query("""
           SELECT p
           FROM Post p          
           JOIN FETCH p.news n        
           JOIN FETCH p.member m  
           WHERE p.member.id = :userId
           ORDER BY p.createdAt DESC
    """)
    List<Post> findPostsWithDetailByUserId(@Param("userId") Long userId);

    @Query("""
            SELECT c
            FROM Comment c
            JOIN FETCH c.post p
            JOIN FETCH p.member m
            WHERE c.member.id = :userId
            ORDER BY c.createdAt DESC
    """)
    List<Comment> findPostsCommentedByUserId(Long userId);
}