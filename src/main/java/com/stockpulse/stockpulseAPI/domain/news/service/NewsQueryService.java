package com.stockpulse.stockpulseAPI.domain.news.service;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.member.repository.MemberRepository;
import com.stockpulse.stockpulseAPI.domain.news.dto.GeminiRequestDTO;
import com.stockpulse.stockpulseAPI.domain.news.dto.GeminiResponseDTO;
import com.stockpulse.stockpulseAPI.domain.news.dto.NewsRequestDTO;
import com.stockpulse.stockpulseAPI.domain.news.dto.NewsResponseDTO;
import com.stockpulse.stockpulseAPI.domain.news.entity.Impact;
import com.stockpulse.stockpulseAPI.domain.news.entity.News;
import com.stockpulse.stockpulseAPI.domain.news.entity.Sentiment;
import com.stockpulse.stockpulseAPI.domain.news.repository.ImpactRepository;
import com.stockpulse.stockpulseAPI.domain.news.repository.MemberScrapNewsRepository;
import com.stockpulse.stockpulseAPI.domain.news.repository.NewsRepository;
import com.stockpulse.stockpulseAPI.domain.stock.entity.Stock;
import com.stockpulse.stockpulseAPI.domain.stock.entity.StockTick;
import com.stockpulse.stockpulseAPI.domain.stock.repository.StockTickRepository;
import com.stockpulse.stockpulseAPI.global.apiPayload.code.status.ErrorStatus;
import com.stockpulse.stockpulseAPI.global.apiPayload.exception.handler.MemberHandler;
import com.stockpulse.stockpulseAPI.global.apiPayload.exception.handler.NewsHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.stockpulse.stockpulseAPI.domain.news.converter.NewsConverter.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsQueryService {

    private final NewsRepository newsRepository;
    private final MemberRepository memberRepository;
    private final StockTickRepository stockTickRepository;
    private final ImpactRepository impactRepository;
    private final MemberScrapNewsRepository memberScrapNewsRepository;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.prompts.news-summary}")
    private String newsSummaryPrompt;

    // 뉴스 상세 조회
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
                    createStockInfoWithTick(impact.getStock(), impact, i + 1)
            );
        }
        return toNewsDetailResponseDTO(news, newsDetailStockDTOS, isScrapped, sentiment);
    }

    // 메인 뉴스 조회
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

        StockTick stockTick = getLatestStockTick(highestImpactStock);
        BigDecimal currentPrice = stockTick != null ? stockTick.getClosePrice() : BigDecimal.ZERO;
        BigDecimal priceChange = stockTick != null ? stockTick.getChangeRate() : BigDecimal.ZERO;
        NewsResponseDTO.NewsOverviewStockDTO stockInfo = toNewsOverviewStockDTO(highestImpactStock, highestImpact, currentPrice, priceChange);
        return toNewsOverviewDTO(relatedNews, scrapped, sentiment, stockInfo);
    }

    // 뉴스 필터링
    public List<NewsResponseDTO.NewsDTO> getFilteredNews(NewsRequestDTO.NewsFilterRequest request, Long memberId) {
        List<News> filteredNews = newsRepository.dynamicQueryWithBooleanBuilder(request, memberId);
        
        // 영향도순 정렬
        if (request.getSort() != null && request.getSort() == NewsRequestDTO.NewsFilterRequest.SortType.IMPACT) {
            filteredNews = filteredNews.stream()
                .sorted((n1, n2) -> {
                    var max1 = n1.getImpacts().stream()
                        .map(impact -> impact.getImpactRate().abs())
                        .max(java.math.BigDecimal::compareTo)
                        .orElse(java.math.BigDecimal.ZERO);
                    var max2 = n2.getImpacts().stream()
                        .map(impact -> impact.getImpactRate().abs())
                        .max(java.math.BigDecimal::compareTo)
                        .orElse(java.math.BigDecimal.ZERO);
                    return max2.compareTo(max1);
                })
                .toList();
        }
        
        return filteredNews.stream()
                .map(news -> {
                    Member member = memberRepository.findById(memberId)
                            .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
                    boolean scrapped = memberScrapNewsRepository.findByMemberAndNews(member, news).isPresent();

                    Impact maxImpact = news.getImpacts().stream()
                            .max((i1, i2) -> i1.getImpactRate().abs().compareTo(i2.getImpactRate().abs()))
                            .orElse(null);
                    
                    Sentiment sentiment = maxImpact != null ? determineSentiment(maxImpact) : Sentiment.NEUTRAL;

                    NewsResponseDTO.NewsDetailStockDTO stockInfo = maxImpact != null ? 
                            createStockInfoWithTick(maxImpact.getStock(), maxImpact, 1) : null;
                    
                    return toNewsDTO(news, scrapped, sentiment, stockInfo);
                })
                .toList();
    }

    // 뉴스 요약
    public NewsResponseDTO.newsSummaryDTO summaryNews(Long newsId) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new NewsHandler(ErrorStatus.NEWS_NOT_FOUND));

        String newsContent = news.getContent();
        if (newsContent == null || newsContent.trim().isEmpty()) {
            throw new NewsHandler(ErrorStatus.NEWS_CONTENT_NOT_FOUND);
        }

        String geminiURL = geminiApiUrl + "?key=" + geminiApiKey;

        String prompt = newsSummaryPrompt + newsContent;

        GeminiRequestDTO requestDto = GeminiRequestDTO.builder()
                .contents(List.of(
                        GeminiRequestDTO.Content.builder()
                                .parts(List.of(
                                        GeminiRequestDTO.Part.builder()
                                                .text(prompt)
                                                .build()
                                ))
                                .build()
                ))
                .build();

        WebClient webClient = WebClient.builder().build();
        try {
            GeminiResponseDTO response = webClient.post()
                    .uri(geminiURL)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(GeminiResponseDTO.class)
                    .block();

            String rawSummary = response.getCandidates().get(0).getContent().getParts().get(0).getText();
            String cleanedSummary = cleanSummaryText(rawSummary);
            return NewsResponseDTO.newsSummaryDTO.builder()
                    .content(cleanedSummary)
                    .build();

        } catch (Exception e) {
            throw new NewsHandler(ErrorStatus.GEMINI_NOT_WORK);
        }
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

    private StockTick getLatestStockTick(Stock stock) {
        return stockTickRepository.findByStock(stock).orElse(null);
    }

    private NewsResponseDTO.NewsDetailStockDTO createStockInfoWithTick(Stock stock, Impact impact, Integer rank) {
        StockTick stockTick = getLatestStockTick(stock);
        BigDecimal currentPrice = stockTick != null ? stockTick.getClosePrice() : BigDecimal.ZERO;
        BigDecimal priceChange = stockTick != null ? stockTick.getChangeRate() : BigDecimal.ZERO;
        
        return toNewsDetailStockDTO(stock, impact, currentPrice, priceChange, rank);
    }

    private String cleanSummaryText(String rawSummary) {
        if (rawSummary == null || rawSummary.trim().isEmpty()) {
            return "요약을 생성할 수 없습니다.";
        }
        
        return rawSummary
                .replaceAll("\\\\n", "\n")
                .replaceAll("\\\\\"", "\"")
                .replaceAll("\\\\t", " ")
                .replaceAll("\\s+", " ")
                .replaceAll("^[\"'`]|[\"'`]$", "")
                .trim();
    }
}
