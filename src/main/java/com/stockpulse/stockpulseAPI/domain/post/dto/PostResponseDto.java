package com.stockpulse.stockpulseAPI.domain.post.dto;

import com.stockpulse.stockpulseAPI.domain.post.entity.Comment;
import lombok.*;

import java.util.List;

public class PostResponseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostSummaryDto {
        private Long postId;
        private String title;
        private String contentSummary;
        private String createdAt;
        private String author;
        private Integer commentCount;
        private Integer voteCount;

        private String newsImageUrl;
        private String newsTitle;
        private String newsPublishedDate;
        private String newsPublisher;

        private String stockImageUrl;
        private String stockName;
        private Long stockPrice;
        private Float stockChangeRate;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class PostDetailDto {
        private Long postId;
        private String author;
        private String updatedAt;
        private String title;
        private String content;

        private String newsImageUrl;
        private Long newsId;
        private String newsTitle;
        private String newsPublishedDate;
        private String press;
        private boolean isNewsScrapped;

        private Long stockId;
        private String stockImageUrl;
        private String stockName;
        private Integer stockPrice;
        private Float stockChangeRate;
        private boolean isStockOwned;
        private boolean isStockFavorite;

        private VoteResponseDTO voteSummary;

        private Integer commentCount;
        private List<CommentResponseDto> comments;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class VoteResponseDTO{
        private Long postId;
        private Long buy;
        private Long sell;
        private Long hold;
        private Long total;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommentResponseDto {
        private Long commentId;
        private String content;
        private String createdAt;
        private String author;
    }
}
