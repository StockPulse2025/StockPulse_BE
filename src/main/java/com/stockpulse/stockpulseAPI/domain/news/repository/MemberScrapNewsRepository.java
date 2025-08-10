package com.stockpulse.stockpulseAPI.domain.news.repository;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.news.entity.MemberScrapNews;
import com.stockpulse.stockpulseAPI.domain.news.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberScrapNewsRepository extends JpaRepository<MemberScrapNews, Long> {
    Optional<MemberScrapNews> findByMemberAndNews(Member member, News news);
}
