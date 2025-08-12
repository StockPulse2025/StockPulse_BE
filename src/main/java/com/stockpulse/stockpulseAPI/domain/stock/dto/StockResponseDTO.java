package com.stockpulse.stockpulseAPI.domain.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class StockResponseDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StockFavoriteStatusDTO {
        private boolean isFavorite;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StockOwnedStatusDTO {
        private boolean isOwned;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StockSummaryDTO {
        private String name;
        private String symbol;
        private String imageUrl;
    }
}
