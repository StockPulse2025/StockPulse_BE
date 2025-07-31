package com.stockpulse.stockpulseAPI.domain.news.service;

import com.stockpulse.stockpulseAPI.domain.news.dto.newsRequestDTO;
import com.stockpulse.stockpulseAPI.domain.news.entity.Impact;
import com.stockpulse.stockpulseAPI.domain.news.entity.News;
import com.stockpulse.stockpulseAPI.domain.news.repository.ImpactRepository;
import com.stockpulse.stockpulseAPI.domain.news.repository.NewsRepository;
import com.stockpulse.stockpulseAPI.domain.stock.entity.Stock;
import com.stockpulse.stockpulseAPI.domain.stock.repository.StockRepository;
import com.stockpulse.stockpulseAPI.global.apiPayload.code.status.ErrorStatus;
import com.stockpulse.stockpulseAPI.global.apiPayload.exception.handler.StockHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsCommandService {

    private final NewsRepository newsRepository;
    private final ImpactRepository impactRepository;
    private final StockRepository stockRepository;

    @Transactional
    public void acceptDataFromPipeline(newsRequestDTO.NewsDataPostRequestDTO request) {
        News news = newsRepository.findByUrl(request.getNewsUrl())
                .orElse(null);
        if(news == null){
            News newNews = News.builder()
                    .title(request.getNewsTitle())
                    .image(request.getNewsImage())
                    .url(request.getNewsUrl())
                    .press(request.getPress())
                    .publishedDate(request.getPublishedDate())
                    .content(request.getContent())
                    .build();
            newsRepository.save(newNews);
            saveImpactFromNewsData(newNews, request.getRelatedStocks());
            return;
        }
        saveImpactFromNewsData(news, request.getRelatedStocks());
    }

    private void saveImpactFromNewsData(News news, List<newsRequestDTO.NewsRelatedStocksDataDTO> request) {
        List<Impact> impacts = new ArrayList<>();
        for (newsRequestDTO.NewsRelatedStocksDataDTO data : request) {
            stockRepository.findBySymbol(data.getSymbol()).ifPresent(stock -> {
                Impact newImpact = Impact.builder()
                        .stock(stock)
                        .news(news)
                        .impactRate(data.getInfluenceScore())
                        .build();
                impacts.add(newImpact);
            });
        }
        impactRepository.saveAll(impacts);
    }
}