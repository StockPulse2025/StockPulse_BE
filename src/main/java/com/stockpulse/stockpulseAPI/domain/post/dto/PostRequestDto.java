package com.stockpulse.stockpulseAPI.domain.post.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PostRequestDto {
    private Long newsId;
    private String title;
    private String content;
    private Long stockId;
    private boolean requireVote;

    @Getter
    public static class VoteParticipationDTO {
        private Integer voteType;
    }

    @Getter
    public static class DeletePostDTO {
        private List<Long> postIds;
    }

    @Getter
    public static class DeleteCommentDTO {
        private List<Long> commentIds;
    }

    @Getter
    public static class CreateCommentDTO {
        private String content;
    }
}
