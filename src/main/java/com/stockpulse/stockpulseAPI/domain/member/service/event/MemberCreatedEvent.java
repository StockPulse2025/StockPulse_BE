package com.stockpulse.stockpulseAPI.domain.member.service.event;

import lombok.Getter;

@Getter
public class MemberCreatedEvent {
    private Long memberId;

    public MemberCreatedEvent(Long memberId) {
        this.memberId = memberId;
    }
}
