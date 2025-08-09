package com.stockpulse.stockpulseAPI.domain.news.service;

import com.stockpulse.stockpulseAPI.domain.news.dto.NewsRequestDTO;
import com.stockpulse.stockpulseAPI.domain.news.entity.Impact;
import com.stockpulse.stockpulseAPI.domain.news.entity.News;
import com.stockpulse.stockpulseAPI.domain.news.repository.ImpactRepository;
import com.stockpulse.stockpulseAPI.domain.news.repository.NewsRepository;
import com.stockpulse.stockpulseAPI.domain.stock.repository.StockRepository;
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
    public void acceptDataFromPipeline(NewsRequestDTO.NewsDataPostRequestDTO request) {
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
                    .reason(request.getReason())
                    .build();
            newsRepository.save(newNews);
            saveImpactFromNewsData(newNews, request.getRelatedStocks());
            return;
        }
        saveImpactFromNewsData(news, request.getRelatedStocks());
    }

    private void saveImpactFromNewsData(News news, List<NewsRequestDTO.NewsRelatedStocksDataDTO> request) {
        List<Impact> impacts = new ArrayList<>();
        for (NewsRequestDTO.NewsRelatedStocksDataDTO data : request) {
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