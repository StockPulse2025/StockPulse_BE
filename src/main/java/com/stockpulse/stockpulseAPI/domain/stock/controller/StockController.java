package com.stockpulse.stockpulseAPI.domain.stock.controller;

import com.stockpulse.stockpulseAPI.domain.news.dto.NewsResponseDTO;
import com.stockpulse.stockpulseAPI.domain.stock.dto.StockResponseDTO;
import com.stockpulse.stockpulseAPI.domain.stock.service.StockCommandService;
import com.stockpulse.stockpulseAPI.global.apiPayload.ApiResponse;
import com.stockpulse.stockpulseAPI.global.security.handler.annotation.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stocks")
public class StockController {

    private final StockCommandService stockCommandService;

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
        StockResponseDTO.StockFavoriteStatusDTO result = stockCommandService.toggleStockFavorite(stockId,memberId);
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
        StockResponseDTO.StockOwnedStatusDTO result = stockCommandService.toggleStockOwned(stockId,memberId);
        return ApiResponse.onSuccess(result);
    }
}
