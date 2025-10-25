package com.stockpulse.stockpulseAPI.domain.post.service;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.member.repository.MemberRepository;
import com.stockpulse.stockpulseAPI.domain.news.dto.NewsResponseDTO;
import com.stockpulse.stockpulseAPI.domain.news.entity.News;
import com.stockpulse.stockpulseAPI.domain.news.exception.NewsException;
import com.stockpulse.stockpulseAPI.domain.news.repository.NewsRepository;
import com.stockpulse.stockpulseAPI.domain.post.dto.PostRequestDto;
import com.stockpulse.stockpulseAPI.domain.post.dto.PostResponseDTO;
import com.stockpulse.stockpulseAPI.domain.post.entity.*;
import com.stockpulse.stockpulseAPI.domain.post.entity.enums.VoteOption;
import com.stockpulse.stockpulseAPI.domain.post.repository.CommentRepository;
import com.stockpulse.stockpulseAPI.domain.post.repository.PostRepository;
import com.stockpulse.stockpulseAPI.domain.post.repository.VoteRecordRepository;
import com.stockpulse.stockpulseAPI.domain.post.repository.VoteRepository;
import com.stockpulse.stockpulseAPI.domain.stock.converter.StockConverter;
import com.stockpulse.stockpulseAPI.domain.stock.dto.StockResponseDTO;
import com.stockpulse.stockpulseAPI.domain.stock.entity.Stock;
import com.stockpulse.stockpulseAPI.domain.stock.entity.StockTick;
import com.stockpulse.stockpulseAPI.domain.stock.repository.MemberFavoriteStockRepository;
import com.stockpulse.stockpulseAPI.domain.stock.repository.MemberOwnStockRepository;
import com.stockpulse.stockpulseAPI.domain.stock.repository.StockRepository;
import com.stockpulse.stockpulseAPI.domain.stock.repository.StockTickRepository;
import com.stockpulse.stockpulseAPI.global.apiPayload.code.status.ErrorStatus;
import com.stockpulse.stockpulseAPI.global.apiPayload.exception.handler.MemberHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    private final StockTickRepository stockTickRepository;
    private final MemberFavoriteStockRepository memberFavoriteStockRepository;
    private final MemberOwnStockRepository memberOwnStockRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public List<PostResponseDTO.SummaryDTO> getPostList(int page, int size, String sort, Long memberId) {
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

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        return postPage.getContent().stream()
                .map(post -> PostResponseDTO.SummaryDTO.builder()
                        .postId(post.getId())
                        .title(post.getTitle())
                        .contentSummary(post.getContent().length() <= 40 ?
                                post.getContent() :
                                post.getContent().substring(0, 40) + "..." )
                        .createdAt(post.getCreatedAt().toString())
                        .author(post.getMember().getNickname())
                        .commentCount(post.getCommentCount())
                        .has_voted(voteRepository.existsByPost(post))
                        .voteCount(post.getVoteCount())

                        .newsImageUrl(post.getNews().getImage())
                        .newsTitle(post.getNews().getTitle())
                        .newsPublishedDate(post.getNews().getPublishedDate().toString())
                        .newsPublisher(post.getNews().getPress())

                        .stockDetail(
                                createStockDetailDTO(
                                        post.getStock(),
                                        getStockDataFromRedis(post.getStock().getSymbol()),
                                        memberFavoriteStockRepository.existsByMemberAndStock(member, post.getStock()),
                                        memberOwnStockRepository.existsByMemberAndStock(member, post.getStock())
                                )
                        )
                        .build()
                )
                .collect(Collectors.toList());
    }

    private List<String> getStockDataFromRedis(String stockSymbol) {
        try {
            String redisKey = "stock:tick:" + stockSymbol;
            HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
            List<String> fields
                    = Arrays.asList("closePrice", "changeRate", "changeAmount","tradingValue","tradingValue");
            return hashOps.multiGet(redisKey, fields);
        } catch (Exception e) {
            // Redis 연결 실패, 타임아웃 등 모든 예외 상황에서 null 반환
            return null;
        }
    }

    private StockResponseDTO.StockDetailDTO createStockDetailDTO(Stock stock, List<String> redisValues,
                                                                 boolean isFavorite, boolean isOwned) {
        // Redis 캐시 미스 시 데이터베이스 fallback
        if (redisValues == null || redisValues.stream().allMatch(Objects::isNull) ||
                redisValues.stream().anyMatch(value -> value == null || value.trim().isEmpty())) {
            Optional<StockTick> latestTick = stockTickRepository.findByStock(stock);
            if (latestTick.isEmpty()) {
                return StockConverter.toStockDetailDTOFault(stock, isFavorite, isOwned);
            }
            return StockConverter.toStockDetailDTOFallBack(stock, latestTick.get(), isFavorite, isOwned);
        }
        return StockConverter.toStockDetailDTO(stock, redisValues, isFavorite, isOwned);
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
    public void deletePosts(PostRequestDto.DeletePostDTO deletePostDto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        List<Post> deleteTargetPosts = postRepository.findAllById(deletePostDto.getPostIds());

        List<Post> postsToDelete = deleteTargetPosts.stream()
                .filter(post -> post.getMember().equals(member))
                .toList();

        postRepository.deleteAll(postsToDelete);
    }

    @Transactional
    public void deleteComments(PostRequestDto.DeleteCommentDTO deleteCommentDto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        List<Comment> deleteTargetComments = commentRepository.findAllById(deleteCommentDto.getCommentIds());

        List<Comment> commentsToDelete = deleteTargetComments.stream()
                .filter(comment -> comment.getMember().equals(member))
                .toList();

        commentRepository.deleteAll(commentsToDelete);
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
    public PostResponseDTO.CommentDTO createComment(Long userId, Long postId, String content) {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new RuntimeException("Member not found")); // TODO : 커스텀 Exception 추가 필요
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found")); // TODO : 커스텀 Exception 추가 필요

        Comment comment = Comment.builder()
                .post(post)
                .member(member)
                .content(content)
                .build();

        commentRepository.save(comment);

        post.setCommentCount(post.getCommentCount() + 1);

        PostResponseDTO.CommentDTO dto = PostResponseDTO.CommentDTO.builder()
                .commentId(comment.getId())
                .content(content)
                .createdAt(post.getCreatedAt().toString())
                .author(member.getNickname())
                .build();

        return dto;
    }

    public PostResponseDTO.DetailDTO getPostDetail(Long userId, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found")); // TODO : 커스텀 Exception 추가 필요
        Member member = memberRepository.findById(userId).orElseThrow(() -> new RuntimeException("Member not found"));// TODO : 커스텀 Exception 추가 필요

        PostResponseDTO.DetailDTO dto = PostResponseDTO.DetailDTO.builder()
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
                .stockDetail(
                        createStockDetailDTO(
                        post.getStock(),
                        getStockDataFromRedis(post.getStock().getSymbol()),
                        memberFavoriteStockRepository.existsByMemberAndStock(member, post.getStock()),
                        memberOwnStockRepository.existsByMemberAndStock(member, post.getStock())
                ))
                .voteSummary(PostResponseDTO.VoteDTO.builder()
                        .postId(post.getId())
                        .buy(post.getVote().getBuy())
                        .sell(post.getVote().getSell())
                        .hold(post.getVote().getHold())
                        .total(post.getVote().getTotal())
                        .build())
                .commentCount(post.getCommentCount())
                .comments(post.getComments().stream().map(comment -> PostResponseDTO.CommentDTO.builder()
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
    public PostResponseDTO.VoteDTO vote(Long userId, long postId, PostRequestDto.VoteParticipationDTO voteParticipationDTO) {
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

        PostResponseDTO.VoteDTO dto = PostResponseDTO.VoteDTO.builder()
                .postId(post.getId())
                .buy(vote.getBuy())
                .sell(vote.getSell())
                .hold(vote.getHold())
                .total(vote.getTotal())
                .build();

        return dto;
    }
}
