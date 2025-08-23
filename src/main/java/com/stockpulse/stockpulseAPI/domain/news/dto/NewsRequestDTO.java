package com.stockpulse.stockpulseAPI.domain.news.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class NewsRequestDTO {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewsDataBatchPostRequestDTO {
        private List<NewsDataPostRequestDTO> newsDataList;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewsDataPostRequestDTO {
        private String newsTitle;
        private String newsUrl;
        private String newsImage;
        private String press;
        private String content;
        private String reason;
        private LocalDateTime publishedDate;
        private List<NewsRelatedStocksDataDTO> relatedStocks;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewsFilterRequest {
        private SortType sort;
        
        public enum SortType {
            LATEST, IMPACT
        }

        private Boolean allStock;
        private Boolean ownedStock;
        private Boolean favoriteStock;

        private List<String> industries;

        private SensitivityFilter positive;
        private SensitivityFilter negative;
        private Boolean neutral;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SensitivityFilter {
        private boolean enabled;
        private int minImpact;
        private int maxImpact;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewsRelatedStocksDataDTO{
        private String stockName;
        private String symbol;
        private BigDecimal influenceScore;
    }
}
