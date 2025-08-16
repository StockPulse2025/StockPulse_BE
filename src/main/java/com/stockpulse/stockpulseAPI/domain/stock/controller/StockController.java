package com.stockpulse.stockpulseAPI.domain.stock.controller;

import com.stockpulse.stockpulseAPI.domain.news.dto.NewsResponseDTO;
import com.stockpulse.stockpulseAPI.domain.stock.dto.StockResponseDTO;
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
}
