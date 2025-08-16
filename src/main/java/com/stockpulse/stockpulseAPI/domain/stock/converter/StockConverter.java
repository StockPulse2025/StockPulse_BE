package com.stockpulse.stockpulseAPI.domain.stock.converter;

import com.stockpulse.stockpulseAPI.domain.stock.dto.StockResponseDTO;
import com.stockpulse.stockpulseAPI.domain.stock.entity.Stock;

public class StockConverter {

    public static StockResponseDTO.StockSearchResultDTO toStockSearchResultDTO(Stock stock) {
        return StockResponseDTO.StockSearchResultDTO.builder()
                .stockId(stock.getId())
                .name(stock.getName())
                .symbol(stock.getSymbol())
                .imageUrl(stock.getImageUrl())
                .build();
    }
}