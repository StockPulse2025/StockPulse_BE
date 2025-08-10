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
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewsRelatedStocksDataDTO{
        private String stockName;
        private String symbol;
        private BigDecimal influenceScore;
    }
}
