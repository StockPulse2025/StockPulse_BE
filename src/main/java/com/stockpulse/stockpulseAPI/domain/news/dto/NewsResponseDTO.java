package com.stockpulse.stockpulseAPI.domain.news.dto;

import com.stockpulse.stockpulseAPI.domain.news.entity.Sentiment;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class NewsResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyLatestNewsDTO {
        private Long newsId;
        private String newsTitle;
        private String newsImage;
        private String press;
        private LocalDateTime publishedDate;
        private Sentiment sentiment;
        private Long stockId;
        private String stockName;
        private String stockImage;
        private BigDecimal influenceScore;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewsTimePointDTO {
        @Setter
        private Integer rank;
        private Long newsId;
        private String newsTitle;
        private String newsImage;
        private String press;
        private LocalDateTime publishedDate;
        private BigDecimal influenceScore;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewsDTO{
        private Long newsId;
        private String newsTitle;
        private String newsUrl;
        private String newsImage;
        private String press;
        private Sentiment sentiment;
        private LocalDateTime publishedDate;
        private Boolean scrapped;
        private NewsDetailStockDTO stockInfo;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewsDetailResponseDTO {
        private Long newsId;
        private String newsTitle;
        private String newsUrl;
        private String newsImage;
        private String press;
        private Sentiment sentiment;
        private LocalDateTime publishedDate;
        private Boolean scrapped;
        private String reason;
        private List<NewsDetailStockDTO> TopImpactStockRank;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewsDetailStockDTO {
        private Integer rank;
        private Long stockId;
        private String stockName;
        private String stockImage;
        private BigDecimal currentPrice;
        private BigDecimal priceChange;
        private String symbol;
        private BigDecimal influenceScore;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewsOverviewDTO {
        private Long newsId;
        private String newsTitle;
        private String newsImage;
        private String press;
        private Sentiment sentiment;
        private LocalDateTime publishedDate;
        private Boolean scrapped;
        private NewsOverviewStockDTO stockInfo;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewsOverviewStockDTO {
        private Long stockId;
        private String stockName;
        private String stockImage;
        private BigDecimal currentPrice;
        private BigDecimal priceChange;
        private String symbol;
        private BigDecimal influenceScore;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ScrapResultDTO {
        private boolean isSaved;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PostPrefillDTO {
        private Long newsId;
        private String newsTitle;
        private String press;
        private String publishedDate;
        private String imageUrl;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class newsSummaryDTO {
        private String content;
    }
}