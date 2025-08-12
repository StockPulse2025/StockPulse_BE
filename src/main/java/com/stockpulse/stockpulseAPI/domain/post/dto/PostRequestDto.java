package com.stockpulse.stockpulseAPI.domain.post.dto;

import lombok.Getter;

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
}
