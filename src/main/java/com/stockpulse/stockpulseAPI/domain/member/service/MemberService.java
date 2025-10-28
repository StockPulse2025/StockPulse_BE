package com.stockpulse.stockpulseAPI.domain.member.service;

import com.stockpulse.stockpulseAPI.domain.member.dto.MemberRequestDTO;
import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.member.repository.MemberRepository;
import com.stockpulse.stockpulseAPI.domain.news.converter.NewsConverter;
import com.stockpulse.stockpulseAPI.domain.news.dto.NewsResponseDTO;
import com.stockpulse.stockpulseAPI.domain.news.entity.Impact;
import com.stockpulse.stockpulseAPI.domain.news.entity.MemberScrapNews;
import com.stockpulse.stockpulseAPI.domain.news.entity.News;
import com.stockpulse.stockpulseAPI.domain.news.entity.Sentiment;
import com.stockpulse.stockpulseAPI.domain.news.repository.ImpactRepository;
import com.stockpulse.stockpulseAPI.domain.news.repository.MemberScrapNewsRepository;
import com.stockpulse.stockpulseAPI.domain.post.dto.PostResponseDTO;
import com.stockpulse.stockpulseAPI.domain.post.entity.Comment;
import com.stockpulse.stockpulseAPI.domain.post.entity.Post;
import com.stockpulse.stockpulseAPI.domain.stock.converter.StockConverter;
import com.stockpulse.stockpulseAPI.domain.stock.dto.StockResponseDTO;
import com.stockpulse.stockpulseAPI.domain.stock.entity.MemberFavoriteStock;
import com.stockpulse.stockpulseAPI.domain.stock.entity.MemberOwnStock;
import com.stockpulse.stockpulseAPI.domain.stock.entity.Stock;
import com.stockpulse.stockpulseAPI.domain.stock.entity.StockTick;
import com.stockpulse.stockpulseAPI.domain.stock.repository.MemberFavoriteStockRepository;
import com.stockpulse.stockpulseAPI.domain.stock.repository.MemberOwnStockRepository;
import com.stockpulse.stockpulseAPI.domain.stock.repository.StockTickRepository;
import com.stockpulse.stockpulseAPI.global.apiPayload.code.status.ErrorStatus;
import com.stockpulse.stockpulseAPI.global.apiPayload.exception.handler.MemberHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final ImpactRepository impactRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final MemberScrapNewsRepository memberScrapNewsRepository;
    private final MemberFavoriteStockRepository memberFavoriteStockRepository;
    private final MemberOwnStockRepository memberOwnStockRepository;
    private final StockTickRepository stockTickRepository;

    public String getNickname(Long userId) {
        return memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Member not found")) // TODO : 커스텀 Exception 추가 필요
                .getNickname();
    }

    @Transactional
    public void updateNickname(Long userId, MemberRequestDTO.UpdateDTO request) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Member not found"));// TODO : 커스텀 Exception 추가 필요

        member.setNickname(request.getNickname());
    }

    public List<NewsResponseDTO.NewsOverviewDTO> getScrapNews(Long userId) {
        List<MemberScrapNews> scrapNewsList = memberRepository.findScrappedNewsWithNewsByUserId(userId);

        if(scrapNewsList.isEmpty()) {
            return Collections.emptyList();
        }

        List<News> newsList = scrapNewsList.stream()
                                .map(MemberScrapNews::getNews)
                                .distinct()
                                .toList();

        List<Impact> topImpacts = impactRepository.findTopImpactsForNewsList(newsList);

        Map<News, Impact> topImpactsMap = topImpacts.stream()
                .collect(Collectors.toMap(Impact::getNews, impact -> impact));

        // 5. 스크랩 목록을 순회하며 Map을 이용해 최종 DTO를 만듭니다.
        return scrapNewsList.stream()
                .map(scrap -> {
                    News news = scrap.getNews();
                    Impact topImpact = topImpactsMap.get(news); // Map에서 해당 뉴스의 Top Impact를 찾음

                    NewsResponseDTO.NewsOverviewStockDTO stockDTO
                            = NewsConverter.toNewsOverviewStockDTO(
                                topImpact.getStock(),
                                topImpact,
                                BigDecimal.valueOf(10.01),
                                BigDecimal.valueOf(10.0)
                            );

                    Sentiment sentiment = parseSentiment(topImpact);
                    return NewsConverter.toNewsOverviewDTO(news,
                            true,
                            sentiment,
                            stockDTO
                    );
                })
                .collect(Collectors.toList());
    }

    public List<PostResponseDTO.MemberPostPreviewDTO> getPublishedPosts(Long userId) {
        List<Post> posts = memberRepository.findPostsWithDetailByUserId(userId);

        return posts.stream()
                .map(PostResponseDTO.MemberPostPreviewDTO::from)
                .collect(Collectors.toList());
    }

    public List<PostResponseDTO.MemberCommentPostPreviewDTO> getCommentedPosts(Long userId) {
        List<Comment> comments = memberRepository.findPostsCommentedByUserId(userId);

        return comments.stream()
                .map(comment -> PostResponseDTO.MemberCommentPostPreviewDTO
                        .from(comment.getPost(), comment))
                .collect(Collectors.toList());
    }

    public List<StockResponseDTO.StockSimpleDTO> getUserStocks(Long memberId, String type) {
        List<Stock> stockList;
        if (type.equals("OWN")) {
            List<MemberOwnStock> memberOwnStocks = memberOwnStockRepository.findByMemberId(memberId);
            stockList = memberOwnStocks.stream()
                    .map(MemberOwnStock::getStock)
                    .collect(Collectors.toList());
        } else if (type.equals("FAVORITE")) {
            List<MemberFavoriteStock> favoriteStocks = memberFavoriteStockRepository.findByMemberId(memberId);
            stockList = favoriteStocks.stream()
                    .map(MemberFavoriteStock::getStock)
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("Invalid type: " + type);
        }

        List<StockResponseDTO.StockSimpleDTO> result = stockList.stream()
                .map(stock -> {
                    String stockSymbol = stock.getSymbol();
                    List<String> redisValues = getStockDataFromRedis(stockSymbol);
                    return createStockSimpleDTO(stock, redisValues);
                })
                .collect(Collectors.toList());
        return result;
    }

    private List<String> getStockDataFromRedis(String stockSymbol) {
        try {
            String redisKey = "stock:tick:" + stockSymbol;
            HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
            List<String> fields
                    = Arrays.asList("closePrice", "changeRate", "changeAmount","tradingValue","tradingValue");
            return hashOps.multiGet(redisKey, fields);
        } catch (Exception e) {
            // Redis 연결 실패, 타임아웃 등 모든 예외 상황에서 null 반환
            return null;
        }
    }

    private StockResponseDTO.StockSimpleDTO createStockSimpleDTO(Stock stock, List<String> redisValues) {
        // Redis 캐시 미스 시 데이터베이스 fallback
        if (redisValues == null || redisValues.stream().allMatch(Objects::isNull) ||
                redisValues.stream().anyMatch(value -> value == null || value.trim().isEmpty())) {
            Optional<StockTick> latestTick = stockTickRepository.findByStock(stock);
            return StockConverter.toStockSimpleDTO(
                    stock,
                    latestTick.get().getClosePrice(),
                    latestTick.get().getChangeRate());
        }
        return StockConverter.toStockSimpleDTO(
                stock,
                new BigDecimal(redisValues.get(0)),
                new BigDecimal(redisValues.get(1)));
    }

    Sentiment parseSentiment(Impact impact) {
        BigDecimal rate = impact.getImpactRate();
        if (rate.compareTo(BigDecimal.ZERO) > 0) {
            return Sentiment.POSITIVE;
        } else if (rate.compareTo(BigDecimal.ZERO) < 0) {
            return Sentiment.NEGATIVE;
        }
        return Sentiment.NEUTRAL;
    }
}
