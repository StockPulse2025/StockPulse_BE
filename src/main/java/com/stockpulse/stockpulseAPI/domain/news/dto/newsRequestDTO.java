package com.stockpulse.stockpulseAPI.domain.news.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class newsRequestDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewsDataPostRequestDTO {
        private String newsTitle;
        private String newsUrl;
        private String newsImage;
        private String press;
        private String content;
        private LocalDateTime publishedDate;
        private List<NewsRelatedStocksDataDTO> relatedStocks;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewsRelatedStocksDataDTO{
        private String stockName;
        private String symbol;
        private BigDecimal influenceScore;
        private String reason;
    }
}
