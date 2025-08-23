package com.stockpulse.stockpulseAPI.domain.stock.service;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.member.repository.MemberRepository;
import com.stockpulse.stockpulseAPI.domain.news.converter.NewsConverter;
import com.stockpulse.stockpulseAPI.domain.news.dto.NewsResponseDTO;
import com.stockpulse.stockpulseAPI.domain.news.entity.Impact;
import com.stockpulse.stockpulseAPI.domain.news.entity.News;
import com.stockpulse.stockpulseAPI.domain.news.repository.ImpactRepository;
import com.stockpulse.stockpulseAPI.domain.news.repository.NewsRepository;
import com.stockpulse.stockpulseAPI.domain.stock.entity.enums.ChartPeriodType;
import com.stockpulse.stockpulseAPI.global.KIS.KISRestApi.KISRestClient;
import com.stockpulse.stockpulseAPI.domain.stock.converter.StockConverter;
import com.stockpulse.stockpulseAPI.domain.stock.dto.StockRequestDTO;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.stockpulse.stockpulseAPI.domain.news.converter.NewsConverter.toNewsTimePointDTO;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockQueryService {

    private static final int INITIAL_RANK = 1;
    
    private final StockRepository stockRepository;
    private final StockTickRepository stockTickRepository;
    private final MemberRepository memberRepository;
    private final NewsRepository newsRepository;
    private final ImpactRepository impactRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final MemberFavoriteStockRepository memberFavoriteStockRepository;
    private final MemberOwnStockRepository memberOwnStockRepository;
    private final KISRestClient kisRestClient;

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

    // 주식 순위
    public List<StockResponseDTO.StockRankDTO> getStockChart(
            StockRequestDTO.RealTimeChartType chartType, Long memberId) {

        Member member = getMemberById(memberId);
        List<Stock> stockList = stockRepository.findAll();

        List<StockResponseDTO.StockRankDTO> resultList = new ArrayList<>();

        for (Stock stock : stockList) {
            boolean isFavorite = memberFavoriteStockRepository.existsByMemberAndStock(member, stock);
            boolean isOwned = memberOwnStockRepository.existsByMemberAndStock(member, stock);

            List<String> redisValues = getStockDataFromRedis(stock.getSymbol());

            StockResponseDTO.StockRankDTO dto = createStockRankDTO(
                    stock, redisValues, isFavorite, isOwned
            );
            resultList.add(dto);
        }
        
        sortStocksByChartType(resultList, chartType);
        
        return selectTop10WithRanking(resultList);
    }

    // 기간별 종목 캔들 차트 데이터 조회
    public StockResponseDTO.StockCandleListDTO getStockCandleData(
            Long stockId, StockRequestDTO.ChartPeriodType period){
        Stock stock = getStockById(stockId);
        return kisRestClient.getStockCandleListDTO(stock, period);
    }

    // 종목 특정 시점별 뉴스 조회
    public List<NewsResponseDTO.NewsTimePointDTO> getNewsTimePoint(
            Long stockId, ChartPeriodType chartPeriodType, String date) {

        Stock stock = getStockById(stockId);

        LocalDateTime baseDate = parseDate(date);
        LocalDateTime[] dateRange = calculateDateRange(baseDate, chartPeriodType);

        LocalDateTime startDate = dateRange[0];
        LocalDateTime endDate = dateRange[1];

        List<News> newsList = newsRepository.findByStockAndDateRange(stockId, startDate, endDate);

        List<NewsResponseDTO.NewsTimePointDTO> result = new ArrayList<>();
        int rank = INITIAL_RANK;

        for(News news : newsList) {
            Optional<Impact> impact = impactRepository.findByNewsAndStock(news, stock);
            if(impact.isPresent()) {
                result.add(toNewsTimePointDTO(news, impact.get(), rank));
                rank++;
            }
        }
        return result;
    }
    
    private LocalDateTime parseDate(String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return date.atStartOfDay();
        } catch (Exception e) {
            throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다. yyyy-MM-dd 형식을 사용하세요: " + dateStr);
        }
    }
    
    private LocalDateTime[] calculateDateRange(LocalDateTime baseDate, ChartPeriodType period) {
        LocalDateTime startDate;
        LocalDateTime endDate;
        
        switch (period) {
            case DAY:
                startDate = baseDate.withHour(0).withMinute(0).withSecond(0);
                endDate = baseDate.withHour(23).withMinute(59).withSecond(59);
                break;
            case WEEK:
                startDate = baseDate.with(java.time.DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0);
                endDate = baseDate.with(java.time.DayOfWeek.SUNDAY).withHour(23).withMinute(59).withSecond(59);
                break;
            case MONTH:
                startDate = baseDate.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
                endDate = baseDate.withDayOfMonth(baseDate.toLocalDate().lengthOfMonth())
                         .withHour(23).withMinute(59).withSecond(59);
                break;
            default:
                throw new StockHandler(ErrorStatus.STOCK_UNSUPPORTED_CHART_PERIOD);
        }
        
        return new LocalDateTime[]{startDate, endDate};
    }
    
    private void sortStocksByChartType(List<StockResponseDTO.StockRankDTO> stockList, 
                                      StockRequestDTO.RealTimeChartType chartType) {
        switch(chartType) {
            case TRADING_VOLUME:
                stockList.sort((a, b)
                        -> b.getTradingVolume().compareTo(a.getTradingVolume()));
                break;
            case TRADING_VALUE:
                stockList.sort((a, b)
                        -> b.getTradingValue().compareTo(a.getTradingValue()));
                break;
            case TOP_GAINERS:
                stockList.sort((a, b)
                        -> b.getChangeRate().compareTo(a.getChangeRate()));
                break;
            case TOP_LOSERS:
                stockList.sort((a, b)
                        -> a.getChangeRate().compareTo(b.getChangeRate()));
                break;
        }
    }
    
    private List<StockResponseDTO.StockRankDTO> selectTop10WithRanking(
            List<StockResponseDTO.StockRankDTO> stockList) {
        List<StockResponseDTO.StockRankDTO> top10List = stockList.stream()
                .limit(10)
                .collect(Collectors.toList());
        
        for (int i = 0; i < top10List.size(); i++) {
            top10List.get(i).setRank(i + 1);
        }
        
        return top10List;
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
            List<String> fields
                    = Arrays.asList("closePrice", "changeRate", "changeAmount","tradingValue","tradingValue");
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

    private StockResponseDTO.StockRankDTO createStockRankDTO(Stock stock, List<String> redisValues,
                                                                 boolean isFavorite, boolean isOwned) {
        // Redis 캐시 미스 시 데이터베이스 폴백
        if (redisValues == null || redisValues.stream().allMatch(Objects::isNull) ||
                redisValues.stream().anyMatch(value -> value == null || value.trim().isEmpty())) {
            Optional<StockTick> latestTick = stockTickRepository.findByStock(stock);
            if (latestTick.isEmpty()) {
                return StockConverter.toStockRankDTOFault(stock, isFavorite, isOwned);
            }
            return StockConverter.toStockRankDTOFallBack(stock, latestTick.get(), isFavorite, isOwned);
        }
        return StockConverter.toStockRankDTO(stock, redisValues, isFavorite, isOwned);
    }
}