package com.stockpulse.stockpulseAPI.domain.member.dto;

import lombok.Getter;

public class MemberRequestDTO {

    @Getter
    public static class UpdateDTO {
        public String nickname;
    }

}
