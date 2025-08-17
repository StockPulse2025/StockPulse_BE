package com.stockpulse.stockpulseAPI.domain.stock.converter;

import com.stockpulse.stockpulseAPI.domain.stock.dto.StockResponseDTO;
import com.stockpulse.stockpulseAPI.domain.stock.entity.Stock;
import com.stockpulse.stockpulseAPI.domain.stock.entity.StockTick;

import java.math.BigDecimal;
import java.util.List;

public class StockConverter {

    public static StockResponseDTO.StockSearchResultDTO toStockSearchResultDTO(Stock stock) {
        return StockResponseDTO.StockSearchResultDTO.builder()
                .stockId(stock.getId())
                .name(stock.getName())
                .symbol(stock.getSymbol())
                .imageUrl(stock.getImageUrl())
                .build();
    }

    public static StockResponseDTO.StockDetailDTO toStockDetailDTO(Stock stock, List<String> values, boolean isFavorite, boolean isOwned) {
        return StockResponseDTO.StockDetailDTO.builder()
                .stockId(stock.getId())
                .name(stock.getName())
                .symbol(stock.getSymbol())
                .imageUrl(stock.getImageUrl())
                .currentPrice(new BigDecimal(values.get(0)))
                .changeRate(new BigDecimal(values.get(1)))
                .changeAmount(new BigDecimal(values.get(2)))
                .isFavorite(isFavorite)
                .isOwned(isOwned)
                .build();
    }

    public static StockResponseDTO.StockDetailDTO toStockDetailDTOFallBack(Stock stock, StockTick latestTick, boolean isFavorite, boolean isOwned) {
        return StockResponseDTO.StockDetailDTO.builder()
                .stockId(stock.getId())
                .name(stock.getName())
                .symbol(stock.getSymbol())
                .imageUrl(stock.getImageUrl())
                .currentPrice(latestTick.getClosePrice())
                .changeRate(latestTick.getChangeRate())
                .changeAmount(latestTick.getChangeAmount())
                .isFavorite(isFavorite)
                .isOwned(isOwned)
                .build();
    }

    public static StockResponseDTO.StockDetailDTO toStockDetailDTOFault(Stock stock, boolean isFavorite, boolean isOwned) {
        return StockResponseDTO.StockDetailDTO.builder()
                .stockId(stock.getId())
                .name(stock.getName())
                .symbol(stock.getSymbol())
                .imageUrl(stock.getImageUrl())
                .currentPrice(new BigDecimal(0))
                .changeRate(new BigDecimal(0))
                .changeAmount(new BigDecimal(0))
                .isFavorite(isFavorite)
                .isOwned(isOwned)
                .build();
    }
}