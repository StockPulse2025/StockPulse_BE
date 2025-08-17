package com.stockpulse.stockpulseAPI.domain.stock.service;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.member.repository.MemberRepository;
import com.stockpulse.stockpulseAPI.domain.stock.converter.StockConverter;
import com.stockpulse.stockpulseAPI.domain.stock.dto.StockResponseDTO;
import com.stockpulse.stockpulseAPI.domain.stock.entity.Stock;
import com.stockpulse.stockpulseAPI.domain.stock.entity.StockTick;
import com.stockpulse.stockpulseAPI.domain.stock.repository.MemberFavoriteStockRepository;
import com.stockpulse.stockpulseAPI.domain.stock.repository.MemberOwnStockRepository;
import com.stockpulse.stockpulseAPI.domain.stock.repository.StockRepository;
import com.stockpulse.stockpulseAPI.domain.stock.repository.StockTickRepository;
import com.stockpulse.stockpulseAPI.global.apiPayload.code.status.ErrorStatus;
import com.stockpulse.stockpulseAPI.global.apiPayload.exception.handler.MemberHandler;
import com.stockpulse.stockpulseAPI.global.apiPayload.exception.handler.StockHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.stockpulse.stockpulseAPI.domain.stock.converter.StockConverter.toStockDetailDTO;
import static com.stockpulse.stockpulseAPI.domain.stock.converter.StockConverter.toStockDetailDTOFallBack;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockQueryService {

    private final StockRepository stockRepository;
    private final StockTickRepository stockTickRepository;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final MemberFavoriteStockRepository memberFavoriteStockRepository;
    private final MemberOwnStockRepository memberOwnStockRepository;

    // 주식 검색
    public List<StockResponseDTO.StockSearchResultDTO> searchStocks(String keyword) {

        String trimmedKeyword = keyword.trim();
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<Stock> searchResult = stockRepository
                .searchStocks(trimmedKeyword);

        return searchResult.stream()
                .map(StockConverter::toStockSearchResultDTO)
                .collect(Collectors.toList());
    }

    // 주식 상세
    public StockResponseDTO.StockDetailDTO getStockDetail(Long stockId, Long memberId) {
        Member member = getMemberById(memberId);
        Stock stock = getStockById(stockId);
        
        boolean isFavorite = memberFavoriteStockRepository.existsByMemberAndStock(member, stock);
        boolean isOwned = memberOwnStockRepository.existsByMemberAndStock(member, stock);

        List<String> redisValues = getStockDataFromRedis(stock.getSymbol());
        
        return createStockDetailDTO(stock, redisValues, isFavorite, isOwned);
    }

    private Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    private Stock getStockById(Long stockId) {
        return stockRepository.findById(stockId)
                .orElseThrow(() -> new StockHandler(ErrorStatus.STOCK_NOT_FOUND));
    }

    private List<String> getStockDataFromRedis(String stockSymbol) {
        try {
            String redisKey = "stock:tick:" + stockSymbol;
            HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
            List<String> fields = Arrays.asList("closePrice", "changeRate", "changeAmount");
            return hashOps.multiGet(redisKey, fields);
        } catch (Exception e) {
            // Redis 연결 실패, 타임아웃 등 모든 예외 상황에서 null 반환
            return null;
        }
    }

    private StockResponseDTO.StockDetailDTO createStockDetailDTO(Stock stock, List<String> redisValues, 
                                                                boolean isFavorite, boolean isOwned) {
        // Redis 캐시 미스 시 데이터베이스 폴백
        if (redisValues == null || redisValues.stream().allMatch(Objects::isNull) || 
            redisValues.stream().anyMatch(value -> value == null || value.trim().isEmpty())) {
            Optional<StockTick> latestTick = stockTickRepository.findByStock(stock);
            if (latestTick.isEmpty()) {
                return StockConverter.toStockDetailDTOFault(stock, isFavorite, isOwned);
            }
            return StockConverter.toStockDetailDTOFallBack(stock, latestTick.get(), isFavorite, isOwned);
        }
        return StockConverter.toStockDetailDTO(stock, redisValues, isFavorite, isOwned);
    }
}