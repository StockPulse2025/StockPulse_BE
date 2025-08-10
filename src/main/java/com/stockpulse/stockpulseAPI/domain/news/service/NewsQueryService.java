package com.stockpulse.stockpulseAPI.domain.news.service;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.member.repository.MemberRepository;
import com.stockpulse.stockpulseAPI.domain.news.dto.NewsRequestDTO;
import com.stockpulse.stockpulseAPI.domain.news.dto.NewsResponseDTO;
import com.stockpulse.stockpulseAPI.domain.news.entity.Impact;
import com.stockpulse.stockpulseAPI.domain.news.entity.MemberScrapNews;
import com.stockpulse.stockpulseAPI.domain.news.entity.News;
import com.stockpulse.stockpulseAPI.domain.news.entity.Sentiment;
import com.stockpulse.stockpulseAPI.domain.news.repository.ImpactRepository;
import com.stockpulse.stockpulseAPI.domain.news.repository.MemberScrapNewsRepository;
import com.stockpulse.stockpulseAPI.domain.news.repository.NewsRepository;
import com.stockpulse.stockpulseAPI.domain.stock.entity.Stock;
import com.stockpulse.stockpulseAPI.domain.stock.repository.StockRepository;
import com.stockpulse.stockpulseAPI.global.apiPayload.code.status.ErrorStatus;
import com.stockpulse.stockpulseAPI.global.apiPayload.exception.handler.MemberHandler;
import com.stockpulse.stockpulseAPI.global.apiPayload.exception.handler.NewsHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.stockpulse.stockpulseAPI.domain.news.converter.NewsConverter.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsQueryService {

    private final NewsRepository newsRepository;
    private final MemberRepository memberRepository;
    private final StockRepository stockRepository;
    private final ImpactRepository impactRepository;
    private final MemberScrapNewsRepository memberScrapNewsRepository;

    public NewsResponseDTO.NewsDetailResponseDTO getNewsDetail(Long newsId, Long userId) {
        News news = newsRepository.findByIdWithImpacts(newsId)
                .orElseThrow(() -> new NewsHandler(ErrorStatus.NEWS_NOT_FOUND));
        List<Impact> impacts = news.getImpacts().stream()
                .sorted((i1, i2) -> i2.getImpactRate().abs().compareTo(i1.getImpactRate().abs()))
                .toList();

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        boolean isScrapped = memberScrapNewsRepository.findByMemberAndNews(member, news).isPresent();

        Sentiment sentiment = determineSentiment(impacts.get(0));

        List<NewsResponseDTO.NewsDetailStockDTO> newsDetailStockDTOS = new ArrayList<>();

        for (int i = 0; i < impacts.size(); i++) {
            Impact impact = impacts.get(i);
            newsDetailStockDTOS.add(
                    toNewsDetailStockDTO(
                            impact.getStock(), impact, BigDecimal.valueOf(10.01), BigDecimal.valueOf(10.0), i + 1) // TODO : Redis 연동 후 수정
            );
        }
        return toNewsDetailResponseDTO(news, newsDetailStockDTOS, isScrapped, sentiment);
    }

    public NewsResponseDTO.NewsOverviewDTO getMainNews(Long memberId) {

        Pageable topOne = PageRequest.of(0,1);
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        List<Impact> topImpacts
                = impactRepository.findTopByCreatedAtAfterOrderByImpactRateDescWithNews(todayStart, topOne);

        Impact highestImpact = topImpacts.get(0);
        Stock highestImpactStock = highestImpact.getStock();
        News relatedNews = highestImpact.getNews();
        Sentiment sentiment = determineSentiment(highestImpact);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        boolean scrapped = memberScrapNewsRepository.findByMemberAndNews(member, relatedNews).isPresent();

        NewsResponseDTO.NewsOverviewStockDTO stockInfo = toNewsOverviewStockDTO(highestImpactStock, highestImpact, BigDecimal.valueOf(10.01), BigDecimal.valueOf(10.0)); // TODO : Redis 연동 후 수정
        return toNewsOverviewDTO(relatedNews, scrapped, sentiment, stockInfo);
    }

    private Sentiment determineSentiment(Impact impact) {
        BigDecimal rate = impact.getImpactRate();
        if (rate.compareTo(BigDecimal.ZERO) > 0) {
            return Sentiment.POSITIVE;
        } else if (rate.compareTo(BigDecimal.ZERO) < 0) {
            return Sentiment.NEGATIVE;
        }
        return Sentiment.NEUTRAL;
    }
}
