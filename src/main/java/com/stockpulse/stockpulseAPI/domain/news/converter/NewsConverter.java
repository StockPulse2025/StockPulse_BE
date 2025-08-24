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

    public static NewsResponseDTO.ScrapResultDTO toScrapNewsResultDTO(boolean isSaved){
        return NewsResponseDTO.ScrapResultDTO.builder()
                .isSaved(isSaved)
                .build();
    }

    public static NewsResponseDTO.NewsOverviewDTO toNewsOverviewDTO(
            News news, boolean scrapped, Sentiment sentiment, NewsResponseDTO.NewsOverviewStockDTO stockInfo){
        return NewsResponseDTO.NewsOverviewDTO.builder()
                .newsId(news.getId())
                .newsTitle(news.getTitle())
                .newsImage(news.getImage())
                .publishedDate(news.getPublishedDate())
                .press(news.getPress())
                .sentiment(sentiment)
                .scrapped(scrapped)
                .stockInfo(stockInfo)
                .build();
    }

    public static NewsResponseDTO.NewsOverviewStockDTO toNewsOverviewStockDTO(Stock stock, Impact impact, BigDecimal currentPrice, BigDecimal priceChange){
        return NewsResponseDTO.NewsOverviewStockDTO.builder()
                .stockId(stock.getId())
                .stockName(stock.getName())
                .stockImage(stock.getImageUrl())
                .currentPrice(currentPrice)
                .priceChange(priceChange)
                .symbol(stock.getSymbol())
                .influenceScore(impact.getImpactRate())
                .build();
    }

    public static NewsResponseDTO.NewsDTO toNewsDTO(News news, boolean scrapped, Sentiment sentiment, NewsResponseDTO.NewsDetailStockDTO stockInfo) {
        return NewsResponseDTO.NewsDTO.builder()
                .newsId(news.getId())
                .newsTitle(news.getTitle())
                .newsUrl(news.getUrl())
                .newsImage(news.getImage())
                .press(news.getPress())
                .sentiment(sentiment)
                .publishedDate(news.getPublishedDate())
                .scrapped(scrapped)
                .stockInfo(stockInfo)
                .build();
    }

    public static NewsResponseDTO.NewsTimePointDTO toNewsTimePointDTO(News news, Impact impact, Integer rank) {
        return NewsResponseDTO.NewsTimePointDTO.builder()
                .rank(rank)
                .newsId(news.getId())
                .newsTitle(news.getTitle())
                .newsImage(news.getImage())
                .press(news.getPress())
                .publishedDate(news.getPublishedDate())
                .influenceScore(impact.getImpactRate())
                .build();
    }

    public static NewsResponseDTO.MyLatestNewsDTO toMyLatestNewsDTO(
            News news, Stock stock, Impact impact, Sentiment sentiment){
        return NewsResponseDTO.MyLatestNewsDTO.builder()
                .newsId(news.getId())
                .newsTitle(news.getTitle())
                .newsImage(news.getImage())
                .publishedDate(news.getPublishedDate())
                .publishedDate(news.getPublishedDate())
                .sentiment(sentiment)
                .stockId(stock.getId())
                .stockName(stock.getName())
                .stockImage(stock.getImageUrl())
                .influenceScore(impact.getImpactRate())
                .build();

    }
}