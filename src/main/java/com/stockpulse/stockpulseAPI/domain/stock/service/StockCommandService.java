package com.stockpulse.stockpulseAPI.domain.stock.service;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.member.repository.MemberRepository;
import com.stockpulse.stockpulseAPI.domain.stock.dto.StockResponseDTO;
import com.stockpulse.stockpulseAPI.domain.stock.entity.MemberFavoriteStock;
import com.stockpulse.stockpulseAPI.domain.stock.entity.MemberOwnStock;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class StockCommandService {

    private final StockRepository stockRepository;
    private final MemberRepository memberRepository;
    private final MemberFavoriteStockRepository memberFavoriteStockRepository;
    private final MemberOwnStockRepository memberOwnStockRepository;
    private final StockTickRepository stockTickRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    // 관심 종목 토글
    public StockResponseDTO.StockFavoriteStatusDTO toggleStockFavorite(Long memberId, Long stockId) {

        Member member = findMemberByIdOrThrow(memberId);
        Stock stock = findStockByIdOrThrow(stockId);
        Optional<MemberFavoriteStock> favoriteStockOpt = memberFavoriteStockRepository.findByMemberAndStock(member, stock);

        if(favoriteStockOpt.isPresent()) {
            memberFavoriteStockRepository.delete(favoriteStockOpt.get());
            return new StockResponseDTO.StockFavoriteStatusDTO(false);
        }else {
            memberFavoriteStockRepository.save(MemberFavoriteStock.builder()
                    .member(member)
                    .stock(stock)
                    .build());
            return new StockResponseDTO.StockFavoriteStatusDTO(true);
        }
    }

    // 보유 주식 토글
    public StockResponseDTO.StockOwnedStatusDTO toggleStockOwned(Long memberId, Long stockId) {

        Member member = findMemberByIdOrThrow(memberId);
        Stock stock = findStockByIdOrThrow(stockId);
        Optional<MemberOwnStock> ownedStockOpt = memberOwnStockRepository.findByMemberAndStock(member, stock);

        if(ownedStockOpt.isPresent()) {
            memberOwnStockRepository.delete(ownedStockOpt.get());
            return new StockResponseDTO.StockOwnedStatusDTO(false);
        }else {
            memberOwnStockRepository.save(MemberOwnStock.builder()
                    .member(member)
                    .stock(stock)
                    .build());
            return new StockResponseDTO.StockOwnedStatusDTO(true);
        }
    }

    // Redis 주식 체결 데이터 DB 저장
    public void saveRedisStockTickDataToDB(){

        List<String> STOCK_CODE = List.of(
                "005930", "000660", "373220", "207940", "005380",
                "012450", "329180", "105560", "000270", "034020",
                "068270", "035420", "042660", "055550", "028260",
                "012330", "035720", "009540", "032830", "005490",
                "086790", "011200", "015760", "051910", "064350",
                "000810", "138040", "402340", "316140", "267260",
                "010140", "033780", "006400", "096770", "010130",
                "024110", "259960", "034730", "030200", "079550",
                "066570", "323410", "003550", "017670", "018260",
                "006800", "000150", "272210", "086280", "009150",
                "003230", "003670", "352820", "267250", "000100",
                "047810", "010120", "005830", "443060", "003490",
                "047050", "042700", "090430", "377300", "071050",
                "010620", "000880", "021240", "000720", "326030",
                "180640", "010950", "064400", "278470", "005940",
                "032640", "016360", "029780", "161390", "034220"
        );

        for (String symbol : STOCK_CODE) {
            try {
                Stock stock = stockRepository.findBySymbol(symbol)
                        .orElseThrow(() -> new StockHandler(ErrorStatus.STOCK_NOT_FOUND));

                Map<Object, Object> tickData = redisTemplate.opsForHash().entries("stock:tick:" + symbol);
                if (tickData.isEmpty()) {
                    continue;
                }

                LocalDate currentDate = LocalDate.parse(((String) tickData.get("date")).substring(0, 10));
                StockTick stockTick = stockTickRepository.findByStock(stock)
                        .orElse(StockTick.builder().stock(stock).build());
                stockTick.updateTick(
                        currentDate,
                        new BigDecimal((String) tickData.get("closePrice")),
                        new BigDecimal((String) tickData.get("openPrice")),
                        new BigDecimal((String) tickData.get("highPrice")),
                        new BigDecimal((String) tickData.get("lowPrice")),
                        new BigDecimal((String) tickData.get("tradingValue")),
                        new BigDecimal((String) tickData.get("tradingVolume")),
                        new BigDecimal((String) tickData.get("changeAmount")),
                        new BigDecimal((String) tickData.get("changeRate"))
                );
                stockTickRepository.save(stockTick);
            } catch (Exception e) {

            }
        }
    }

    public Member findMemberByIdOrThrow(Long userId) {
        return memberRepository.findById(userId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    public Stock findStockByIdOrThrow(Long StockId) {
        return stockRepository.findById(StockId)
                .orElseThrow(() -> new StockHandler(ErrorStatus.STOCK_NOT_FOUND));
    }
}
