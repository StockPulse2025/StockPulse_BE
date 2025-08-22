package com.stockpulse.stockpulseAPI.domain.post.dto;

import com.stockpulse.stockpulseAPI.domain.post.entity.Post;
import lombok.*;

import java.util.List;

public class PostResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryDTO {
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

        public static SummaryDTO from(Post post) {
            String summary = post.getContent().length() <= 40 ?
                    post.getContent() :
                    post.getContent().substring(0, 40) + "...";

            return SummaryDTO.builder()
                    .postId(post.getId())
                    .title(post.getTitle())
                    .contentSummary(summary)
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
                    .build();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    public static class DetailDTO {
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

        private VoteDTO voteSummary;

        private Integer commentCount;
        private List<CommentDTO> comments;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class VoteDTO {
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
    public static class CommentDTO {
        private Long commentId;
        private String content;
        private String createdAt;
        private String author;
    }
}
