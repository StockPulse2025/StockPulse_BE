package com.stockpulse.stockpulseAPI.domain.stock.controller;

import com.stockpulse.stockpulseAPI.domain.news.dto.NewsResponseDTO;
import com.stockpulse.stockpulseAPI.domain.stock.dto.StockResponseDTO;
import com.stockpulse.stockpulseAPI.domain.stock.dto.StockRequestDTO;
import com.stockpulse.stockpulseAPI.domain.stock.entity.enums.ChartPeriodType;
import com.stockpulse.stockpulseAPI.domain.stock.service.StockCommandService;
import com.stockpulse.stockpulseAPI.domain.stock.service.StockQueryService;
import com.stockpulse.stockpulseAPI.global.apiPayload.ApiResponse;
import com.stockpulse.stockpulseAPI.global.security.handler.annotation.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stocks")
@Tag(name = "Stock", description = "주식 종목 관련 API")
public class StockController {

    private final StockCommandService stockCommandService;
    private final StockQueryService stockQueryService;

    @Operation(
            summary = "관심 종목 토글 API",
            description = "특정 종목에 대해 관심 종목 지정을 추가하거나 취소합니다. " +
                    "해당 종목을 기존에 관심 종목으로 설정하지 않은 경우 관김 종목으로 지정이 추가되고, 이미 되어있는 경우 관심 종목 지정이 취소 됩니다. " +
                    "토글 후 현재 상태를 반환합니다. 토글할 종목 ID를 전달해주세요."
    )
    @PostMapping("/{stockId}/favorites")
    public ApiResponse<StockResponseDTO.StockFavoriteStatusDTO> toggleStockFavorite(
            @AuthUser Long memberId,
            @PathVariable("stockId") Long stockId) {
        StockResponseDTO.StockFavoriteStatusDTO result = stockCommandService.toggleStockFavorite(memberId, stockId);
        return ApiResponse.onSuccess(result);
    }

    @Operation(
            summary = "보유 종목 토글 API",
            description = "특정 종목에 대해 보유 종목 지정을 추가하거나 취소합니다. " +
                    "해당 종목을 기존에 보유 종목으로 설정하지 않은 경우 보유 종목으로 지정이 추가되고, 이미 되어있는 경우 보유 종목 지정이 취소 됩니다. " +
                    "토글 후 현재 상태를 반환합니다. 토글할 종목 ID를 전달해주세요."
    )
    @PostMapping("/{stockId}/owned")
    public ApiResponse<StockResponseDTO.StockOwnedStatusDTO> toggleStockOwned(
            @AuthUser Long memberId,
            @PathVariable("stockId") Long stockId) {
        StockResponseDTO.StockOwnedStatusDTO result = stockCommandService.toggleStockOwned(memberId, stockId);
        return ApiResponse.onSuccess(result);
    }

    @Operation(
            summary = "주식 종목 검색 API",
            description = "키워드로 종목을 검색합니다 (종목 명, 또는 종목 코드)"
    )
    @GetMapping("/search")
    public ApiResponse<List<StockResponseDTO.StockSearchResultDTO>> searchStocks(
            @RequestParam("keyword") String keyword) {
        List<StockResponseDTO.StockSearchResultDTO> result = stockQueryService.searchStocks(keyword);
        return ApiResponse.onSuccess(result);
    }

    @Operation(
            summary = "기간별 종목 캔들차트 데이터 조회 API",
            description = """ 
                    특정 종목의 캔들차트 데이터를 기간별로 조회합니다.
                    - DAY: 일별 캔들차트
                    - WEEK: 주별 캔들차트
                    - MONTH: 월별 캔들차트
                    - 시가, 종가, 고가, 저가, 거래량 정보를 반환합니다.
                    """
    )
    @GetMapping("/{stockId}/candle")
    public ApiResponse<StockResponseDTO.StockCandleListDTO> getStockCandle(
            @PathVariable("stockId") Long stockId,
            @RequestParam("period") StockRequestDTO.ChartPeriodType period
    ) {
        StockResponseDTO.StockCandleListDTO result = stockQueryService.getStockCandleData(stockId, period);
        return ApiResponse.onSuccess(result);
    }

    @Operation(
            summary = "종목 상세 정보 조회 API",
            description = "종목 ID로 종목 상세 정보를 조회합니다."
    )
    @GetMapping("/{stockId}/detail")
    public ApiResponse<StockResponseDTO.StockDetailDTO> getStockDetail(
            @PathVariable("stockId") Long stockId,
            @AuthUser Long memberId) {
        StockResponseDTO.StockDetailDTO result = stockQueryService.getStockDetail(stockId, memberId);
        return ApiResponse.onSuccess(result);
    }

    @Operation(
            summary = "종목 항목 별 순위 차트 조회 API",
            description = """
                    항목 별 종목 순위 차트를 조회합니다.
                    - TRADING_VOLUME: 거래량 상위 10개 종목
                    - TRADING_VALUE: 거래대금 상위 10개 종목
                    - TOP_GAINERS: 상승률 상위 10개 종목
                    - TOP_LOSERS: 하락률 상위 10개 종목
                    """
    )
    @GetMapping("/chart")
    public ApiResponse<List<StockResponseDTO.StockRankDTO>> getStockChart(
            @RequestParam("type") StockRequestDTO.RealTimeChartType chartType,
            @AuthUser Long memberId) {
        List<StockResponseDTO.StockRankDTO> result = stockQueryService.getStockChart(chartType, memberId);
        return ApiResponse.onSuccess(result);
    }


    @Operation(
            summary = "종목 특정 시점 별 영향도 기준 뉴스 리스트 조회 API",
            description = """
    - 종목 캔들 차트의 특정 시점의 영향도 기준 뉴스 리스트를 조회하여 반환합니다.
    - 대상이 되는 종목의 Id, 차트 기간(period), 조회하고자 하는 날짜를 넘겨주세요.
    - 날짜 형식: yyyy-MM-dd (예: 2024-01-15)
    
    차트 기간 타입 및 날짜 예시:
    - DAY: 일봉 기준 (예: 2024-01-15 → 해당 날짜의 뉴스 조회)
    - WEEK: 주봉 기준 (예: 2024-01-15 → 해당 주의 월요일~일요일 뉴스 조회)
    - MONTH: 월봉 기준 (예: 2024-01-15 → 2024년 1월 전체 뉴스 조회)
    """
    )
    @GetMapping("/{stockId}/timepoint")
    public ApiResponse<List<NewsResponseDTO.NewsTimePointDTO>> getStockNewsByTimepoint(
            @PathVariable("stockId") Long stockId,
            @RequestParam("period") ChartPeriodType period,
            @RequestParam("date") String date) {
        List<NewsResponseDTO.NewsTimePointDTO> result
                = stockQueryService.getNewsTimePoint(stockId, period, date);
        return ApiResponse.onSuccess(result);
    }
}