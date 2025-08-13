package com.stockpulse.stockpulseAPI.domain.notification.service.event;

import com.stockpulse.stockpulseAPI.domain.news.entity.Impact;
import lombok.Getter;

import java.util.List;

@Getter
public class ImpactSavedEvent {
    private final List<Impact> impacts;

    public ImpactSavedEvent(List<Impact> impacts) {
        this.impacts = impacts;
    }
}
