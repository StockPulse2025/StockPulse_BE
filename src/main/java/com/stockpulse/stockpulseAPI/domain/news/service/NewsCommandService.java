package com.stockpulse.stockpulseAPI.domain.news.service;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.member.repository.MemberRepository;
import com.stockpulse.stockpulseAPI.domain.news.dto.NewsRequestDTO;
import com.stockpulse.stockpulseAPI.domain.news.dto.NewsResponseDTO;
import com.stockpulse.stockpulseAPI.domain.news.entity.Impact;
import com.stockpulse.stockpulseAPI.domain.news.entity.MemberScrapNews;
import com.stockpulse.stockpulseAPI.domain.news.entity.News;
import com.stockpulse.stockpulseAPI.domain.news.repository.ImpactRepository;
import com.stockpulse.stockpulseAPI.domain.news.repository.MemberScrapNewsRepository;
import com.stockpulse.stockpulseAPI.domain.news.repository.NewsRepository;
import com.stockpulse.stockpulseAPI.domain.notification.service.event.ImpactSavedEvent;
import com.stockpulse.stockpulseAPI.domain.stock.repository.StockRepository;
import com.stockpulse.stockpulseAPI.global.apiPayload.code.status.ErrorStatus;
import com.stockpulse.stockpulseAPI.global.apiPayload.exception.handler.MemberHandler;
import com.stockpulse.stockpulseAPI.global.apiPayload.exception.handler.NewsHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NewsCommandService {

    private final NewsRepository newsRepository;
    private final ImpactRepository impactRepository;
    private final StockRepository stockRepository;
    private final MemberRepository memberRepository;
    private final MemberScrapNewsRepository memberScrapNewsRepository;
    private final ApplicationEventPublisher eventPublisher;

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

    @Transactional
    public NewsResponseDTO.ScrapResultDTO toggleNewsScrap(Long newsId, Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new NewsHandler(ErrorStatus.NEWS_NOT_FOUND));
        Optional<MemberScrapNews> memberScrapNews = memberScrapNewsRepository.findByMemberAndNews(member, news);

        if(memberScrapNews.isPresent()){
            memberScrapNewsRepository.delete(memberScrapNews.get());
            return new NewsResponseDTO.ScrapResultDTO(false);
        }else{
            MemberScrapNews newsScrapNews = MemberScrapNews.builder()
                    .member(member)
                    .news(news)
                    .build();
            memberScrapNewsRepository.save(newsScrapNews);
            return new NewsResponseDTO.ScrapResultDTO(true);
        }
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
//        impactRepository.saveAll(impacts);
        List<Impact> savedImpacts = impactRepository.saveAll(impacts);
        eventPublisher.publishEvent(new ImpactSavedEvent(savedImpacts)); // 이벤트 발행
    }
}