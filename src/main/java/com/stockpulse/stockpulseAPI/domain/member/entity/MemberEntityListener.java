package com.stockpulse.stockpulseAPI.domain.member.entity;

import jakarta.persistence.PostPersist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class MemberEntityListener {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostPersist
    public void postPersist(Member member) {
        eventPublisher.publishEvent(new MemberCreatedEvent(member.getId()));
    }
}
