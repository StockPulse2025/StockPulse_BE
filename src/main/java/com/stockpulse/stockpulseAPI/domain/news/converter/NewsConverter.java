package com.stockpulse.stockpulseAPI.domain.news.converter;

import com.stockpulse.stockpulseAPI.domain.news.dto.NewsResponseDTO;
import com.stockpulse.stockpulseAPI.domain.news.entity.Impact;
import com.stockpulse.stockpulseAPI.domain.news.entity.News;
import com.stockpulse.stockpulseAPI.domain.news.entity.Sentiment;
import com.stockpulse.stockpulseAPI.domain.stock.entity.Stock;

import java.math.BigDecimal;
import java.util.List;

public class NewsConverter {

    public static NewsResponseDTO.NewsDetailResponseDTO toNewsDetailResponseDTO(
            News news, List<NewsResponseDTO.NewsDetailStockDTO> newsDetailStockDTOS,
            boolean scrapped, Sentiment sentiment
    ){
        return NewsResponseDTO.NewsDetailResponseDTO.builder()
                .newsId(news.getId())
                .newsTitle(news.getTitle())
                .newsImage(news.getImage())
                .publishedDate(news.getPublishedDate())
                .press(news.getPress())
                .newsUrl(news.getUrl())
                .scrapped(scrapped)
                .sentiment(sentiment)
                .reason(news.getReason())
                .TopImpactStockRank(newsDetailStockDTOS)
                .build();
    }

    public static NewsResponseDTO.NewsDetailStockDTO toNewsDetailStockDTO(
            Stock stock,
            Impact impact,
            BigDecimal currentPrice,
            BigDecimal priceChange,
            Integer rank){
        return NewsResponseDTO.NewsDetailStockDTO.builder()
                .rank(rank)
                .stockImage(stock.getImageUrl())
                .stockId(stock.getId())
                .stockName(stock.getName())
                .currentPrice(currentPrice)
                .priceChange(priceChange)
                .symbol(stock.getSymbol())
                .influenceScore(impact.getImpactRate())
                .build();
    }
}
