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
import com.stockpulse.stockpulseAPI.domain.stock.repository.StockRepository;
import com.stockpulse.stockpulseAPI.global.apiPayload.code.status.ErrorStatus;
import com.stockpulse.stockpulseAPI.global.apiPayload.exception.handler.MemberHandler;
import com.stockpulse.stockpulseAPI.global.apiPayload.exception.handler.NewsHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.stockpulse.stockpulseAPI.domain.news.converter.NewsConverter.toNewsDetailResponseDTO;
import static com.stockpulse.stockpulseAPI.domain.news.converter.NewsConverter.toNewsDetailStockDTO;

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
                            impact.getStock(), impact, BigDecimal.valueOf(10.01), BigDecimal.valueOf(10.0), i + 1) // Redis 연동 전 더미
            );
        }
        return toNewsDetailResponseDTO(news, newsDetailStockDTOS, isScrapped, sentiment);
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
