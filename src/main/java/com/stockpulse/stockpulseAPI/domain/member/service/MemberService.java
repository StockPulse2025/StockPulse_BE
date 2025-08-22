package com.stockpulse.stockpulseAPI.domain.member.service;

import com.stockpulse.stockpulseAPI.domain.member.dto.MemberRequestDTO;
import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.member.repository.MemberRepository;
import com.stockpulse.stockpulseAPI.domain.news.converter.NewsConverter;
import com.stockpulse.stockpulseAPI.domain.news.dto.NewsResponseDTO;
import com.stockpulse.stockpulseAPI.domain.news.entity.Impact;
import com.stockpulse.stockpulseAPI.domain.news.entity.MemberScrapNews;
import com.stockpulse.stockpulseAPI.domain.news.entity.News;
import com.stockpulse.stockpulseAPI.domain.news.entity.Sentiment;
import com.stockpulse.stockpulseAPI.domain.news.repository.ImpactRepository;
import com.stockpulse.stockpulseAPI.domain.news.repository.MemberScrapNewsRepository;
import com.stockpulse.stockpulseAPI.domain.post.dto.PostResponseDTO;
import com.stockpulse.stockpulseAPI.domain.post.entity.Comment;
import com.stockpulse.stockpulseAPI.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final ImpactRepository impactRepository;
    private final MemberScrapNewsRepository memberScrapNewsRepository;

    public String getNickname(Long userId) {
        return memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Member not found")) // TODO : 커스텀 Exception 추가 필요
                .getNickname();
    }

    @Transactional
    public void updateNickname(Long userId, MemberRequestDTO.UpdateDTO request) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Member not found"));// TODO : 커스텀 Exception 추가 필요

        member.setNickname(request.getNickname());
    }

    public List<NewsResponseDTO.NewsOverviewDTO> getScrapNews(Long userId) {
        List<MemberScrapNews> scrapNewsList = memberRepository.findScrappedNewsWithNewsByUserId(userId);

        if(scrapNewsList.isEmpty()) {
            return Collections.emptyList();
        }

        List<News> newsList = scrapNewsList.stream()
                                .map(MemberScrapNews::getNews)
                                .distinct()
                                .toList();

        List<Impact> topImpacts = impactRepository.findTopImpactsForNewsList(newsList);

        Map<News, Impact> topImpactsMap = topImpacts.stream()
                .collect(Collectors.toMap(Impact::getNews, impact -> impact));

        // 5. 스크랩 목록을 순회하며 Map을 이용해 최종 DTO를 만듭니다.
        return scrapNewsList.stream()
                .map(scrap -> {
                    News news = scrap.getNews();
                    Impact topImpact = topImpactsMap.get(news); // Map에서 해당 뉴스의 Top Impact를 찾음

                    NewsResponseDTO.NewsOverviewStockDTO stockDTO
                            = NewsConverter.toNewsOverviewStockDTO(
                                topImpact.getStock(),
                                topImpact,
                                BigDecimal.valueOf(10.01),
                                BigDecimal.valueOf(10.0)
                            );

                    Sentiment sentiment = parseSentiment(topImpact);
                    return NewsConverter.toNewsOverviewDTO(news,
                            true,
                            sentiment,
                            stockDTO
                    );
                })
                .collect(Collectors.toList());
    }

    public List<PostResponseDTO.SummaryDTO> getPublishedPosts(Long userId) {
        List<Post> posts = memberRepository.findPostsWithDetailByUserId(userId);

        return posts.stream()
                .map(PostResponseDTO.SummaryDTO::from)
                .collect(Collectors.toList());
    }

    public List<PostResponseDTO.SummaryDTO> getCommentedPosts(Long userId) {
        List<Comment> comments = memberRepository.findPostsCommentedByUserId(userId);
        return comments.stream()
                .map(Comment::getPost)
                .map(PostResponseDTO.SummaryDTO::from)
                .collect(Collectors.toList());
    }

    Sentiment parseSentiment(Impact impact) {
        BigDecimal rate = impact.getImpactRate();
        if (rate.compareTo(BigDecimal.ZERO) > 0) {
            return Sentiment.POSITIVE;
        } else if (rate.compareTo(BigDecimal.ZERO) < 0) {
            return Sentiment.NEGATIVE;
        }
        return Sentiment.NEUTRAL;
    }

}
