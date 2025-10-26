package com.stockpulse.stockpulseAPI.domain.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

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

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StockSearchResultDTO{
        private Long stockId;
        private String name;
        private String symbol;
        private String imageUrl;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StockDetailDTO{
        private Long stockId;
        private String name;
        private String symbol;
        private String imageUrl;

        private BigDecimal currentPrice;
        private BigDecimal changeRate;
        private BigDecimal changeAmount;

        private boolean isFavorite;
        private boolean isOwned;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StockRankDTO{
        private Integer rank;
        
        public void setRank(Integer rank) {
            this.rank = rank;
        }

        private Long stockId;
        private String name;
        private String symbol;
        private String imageUrl;

        private BigDecimal currentPrice;
        private BigDecimal changeRate;
        private BigDecimal changeAmount;
        private BigDecimal tradingValue;
        private BigDecimal tradingVolume;

        private boolean isFavorite;
        private boolean isOwned;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StockCandleDTO{
        private String date;

        private String openPrice;
        private String highPrice;
        private String lowPrice;
        private String closePrice;
        private String totalVolume;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StockTickDataDTO {
        private String symbol;
        private BigDecimal currentPrice;
        private BigDecimal changeRate;
        private BigDecimal changeAmount;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StockCandleListDTO{
        private Long stockId;
        private String stockName;
        private String symbol;
        private StockRequestDTO.ChartPeriodType period;
        private List<StockCandleDTO> stockCandleDataList;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StockSimpleDTO{
        private Long stockId;
        private String stockName;
        private String symbol;
        private String imageUrl;

        private BigDecimal currentPrice;
        private BigDecimal changeRate;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MyStockInfluenceResponse{
        private Long stockId;
        private String stockName;
        private String symbol;
        private String imageUrl;

        private BigDecimal currentPrice;
        private BigDecimal changeRate;
        private BigDecimal changeAmount;

        private BigDecimal predictInfluenceScore;
        private Integer relatedIssueCount;
    }
}