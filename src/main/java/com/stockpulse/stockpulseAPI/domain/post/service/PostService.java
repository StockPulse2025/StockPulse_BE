package com.stockpulse.stockpulseAPI.domain.post.service;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.member.repository.MemberRepository;
import com.stockpulse.stockpulseAPI.domain.news.dto.NewsResponseDTO;
import com.stockpulse.stockpulseAPI.domain.news.entity.News;
import com.stockpulse.stockpulseAPI.domain.news.exception.NewsException;
import com.stockpulse.stockpulseAPI.domain.news.repository.NewsRepository;
import com.stockpulse.stockpulseAPI.domain.post.dto.PostRequestDto;
import com.stockpulse.stockpulseAPI.domain.post.dto.PostResponseDto;
import com.stockpulse.stockpulseAPI.domain.post.entity.*;
import com.stockpulse.stockpulseAPI.domain.post.entity.enums.VoteOption;
import com.stockpulse.stockpulseAPI.domain.post.repository.CommentRepository;
import com.stockpulse.stockpulseAPI.domain.post.repository.PostRepository;
import com.stockpulse.stockpulseAPI.domain.post.repository.VoteRecordRepository;
import com.stockpulse.stockpulseAPI.domain.post.repository.VoteRepository;
import com.stockpulse.stockpulseAPI.domain.stock.entity.Stock;
import com.stockpulse.stockpulseAPI.domain.stock.repository.StockRepository;
import com.stockpulse.stockpulseAPI.global.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final MemberRepository memberRepository;
    private final NewsRepository newsRepository;
    private final PostRepository postRepository;
    private final StockRepository stockRepository;
    private final VoteRepository voteRepository;
    private final VoteRecordRepository voteRecordRepository;
    private final CommentRepository commentRepository;

    public List<PostResponseDto.PostSummaryDto> getPostList(int page, int size, String sort) {
        Sort sortOption;

        switch (sort.toLowerCase()) {
            case "popular":
                sortOption = Sort.by(Sort.Direction.DESC, "voteCount");
                break;
            case "comment":
                sortOption = Sort.by(Sort.Direction.DESC, "commentCount");
                break;
            case "latest":
            default:
                sortOption = Sort.by(Sort.Direction.DESC, "createdAt");
        }

        Pageable pageable = PageRequest.of(page, size, sortOption);

        Page<Post> postPage = postRepository.findAll(pageable);

        return postPage.getContent().stream()
                .map(post -> PostResponseDto.PostSummaryDto.builder()
                        .postId(post.getId())
                        .title(post.getTitle())
                        .contentSummary(post.getContent().length() <= 40 ?
                                post.getContent() :
                                post.getContent().substring(0, 40) + "..." )
                        .createdAt(post.getCreatedAt().toString())
                        .author(post.getMember().getNickname())
                        .commentCount(post.getCommentCount())
                        .voteCount(post.getVoteCount())

                        .newsImageUrl(post.getNews().getImage())
                        .newsTitle(post.getNews().getTitle())
                        .newsPublishedDate(post.getNews().getPublishedDate().toString())
                        .newsPublisher(post.getNews().getPress())

                        .stockImageUrl(post.getStock().getImageUrl())
                        .stockName(post.getStock().getName())
//                        .stockPrice(post.getStock().getPrice())
//                        .stockChangeRate(post.getStock().getChangeRate())
                        .build()
                )
                .collect(Collectors.toList());
    }

    public NewsResponseDTO.PostPrefillDTO getPrefillNewsInfo(Long newsId) {
        News news = newsRepository.findById(newsId).orElseThrow(() -> new NewsException(ErrorStatus._INTERNAL_SERVER_ERROR));
        NewsResponseDTO.PostPrefillDTO response = NewsResponseDTO.PostPrefillDTO.builder()
                .newsId(news.getId())
                .newsTitle(news.getTitle())
                .press(news.getPress())
                .publishedDate(news.getPublishedDate().toString())
                .imageUrl(news.getImage())
                .build();

        return response;
    }

    @Transactional
    public Long createPost(Long userId, PostRequestDto dto) {

        Member member = memberRepository.findById(userId).orElseThrow(() -> new RuntimeException("Member not found")); // TODO : 커스텀 Exception 추가 필요
        News news = newsRepository.findById(dto.getNewsId()).orElseThrow(() -> new NewsException(ErrorStatus._INTERNAL_SERVER_ERROR));
        Stock stock = stockRepository.findById(dto.getStockId()).orElseThrow(() -> new RuntimeException("Stock not found"));

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .member(member)
                .news(news)
                .stock(stock)
                .build();

        postRepository.save(post);

        if(dto.isRequireVote()) {
            Vote vote = Vote.builder()
                    .post(post)
                    .build();
            voteRepository.save(vote);
        }

        return post.getId();
    }

    @Transactional
    public PostResponseDto.CommentResponseDto createComment(Long userId, Long postId, String content) {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new RuntimeException("Member not found")); // TODO : 커스텀 Exception 추가 필요
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found")); // TODO : 커스텀 Exception 추가 필요

        Comment comment = Comment.builder()
                .post(post)
                .member(member)
                .content(content)
                .build();

        commentRepository.save(comment);

        post.setCommentCount(post.getCommentCount() + 1);

        PostResponseDto.CommentResponseDto dto = PostResponseDto.CommentResponseDto.builder()
                .commentId(comment.getId())
                .content(content)
                .createdAt(post.getCreatedAt().toString())
                .author(member.getNickname())
                .build();

        return dto;
    }

    public PostResponseDto.PostDetailDto getPostDetail(Long userId, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found")); // TODO : 커스텀 Exception 추가 필요
        Member member = memberRepository.findById(userId).orElseThrow(() -> new RuntimeException("Member not found"));// TODO : 커스텀 Exception 추가 필요

        PostResponseDto.PostDetailDto dto = PostResponseDto.PostDetailDto.builder()
                .postId(post.getId())
                .author(post.getMember().getNickname())
                .updatedAt(post.getUpdatedAt().toString())
                .title(post.getTitle())
                .content(post.getContent())
                .newsId(post.getNews().getId())
                .newsTitle(post.getNews().getTitle())
                .newsPublishedDate(post.getNews().getPublishedDate().toString())
                .press(post.getNews().getPress())
                .isNewsScrapped(member.getMemberScrapNewsList().contains(post.getNews()))
                .newsImageUrl(post.getNews().getImage())
                .stockId(post.getStock().getId())
                .stockName(post.getStock().getName())
                // .stockPrice(post.getStock().getPrice())
                // .stockChangeRate(post.getStock().getChangeRate())
                .stockImageUrl(post.getStock().getImageUrl())
                .isStockOwned(member.getMemberOwnStockList().contains(post.getStock()))
                .isStockFavorite(member.getMemberFavoriteStockList().contains(post.getStock()))
                .voteSummary(PostResponseDto.VoteResponseDTO.builder()
                        .postId(post.getId())
                        .buy(post.getVote().getBuy())
                        .sell(post.getVote().getSell())
                        .hold(post.getVote().getHold())
                        .total(post.getVote().getTotal())
                        .build())
                .commentCount(post.getCommentCount())
                .comments(post.getComments().stream().map(comment -> PostResponseDto.CommentResponseDto.builder()
                        .commentId(comment.getId())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt().toString())
                        .author(comment.getMember().getNickname())
                        .build()).toList())
                .build();

        return dto;
    }

    // TODO : 투표 참여
    @Transactional
    public PostResponseDto.VoteResponseDTO vote(Long userId, long postId, PostRequestDto.VoteParticipationDTO voteParticipationDTO) {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new RuntimeException("Member not found")); // TODO : 커스텀 Exception 추가 필요
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found")); // TODO : 커스텀 Exception 추가 필요
        Vote vote = post.getVote();

        VoteOption voteOption;
        switch (voteParticipationDTO.getVoteType()) {
            case 0:
                voteOption = VoteOption.BUY;
                vote.setBuy(vote.getBuy() + 1);
                break;
            case 1:
                voteOption = VoteOption.SELL;
                vote.setSell(vote.getSell() + 1);
                break;
            case 2:
                voteOption = VoteOption.HOLD;
                vote.setHold(vote.getHold() + 1);
                break;
            default:
                throw new RuntimeException("Invalid vote type");
        }

        VoteRecord voteRecord = VoteRecord.builder()
            .vote(vote)
            .member(member)
            .voteOption(voteOption)
            .build();

        voteRecordRepository.save(voteRecord);

        post.setVoteCount(post.getVoteCount() + 1);
        vote.setTotal(vote.getTotal() + 1);

        PostResponseDto.VoteResponseDTO dto = PostResponseDto.VoteResponseDTO.builder()
                .postId(post.getId())
                .buy(vote.getBuy())
                .sell(vote.getSell())
                .hold(vote.getHold())
                .total(vote.getTotal())
                .build();

        return dto;
    }
}
