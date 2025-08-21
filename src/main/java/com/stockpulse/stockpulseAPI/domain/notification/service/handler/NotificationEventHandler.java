package com.stockpulse.stockpulseAPI.domain.notification.service.handler;

import com.stockpulse.stockpulseAPI.domain.member.entity.MemberCreatedEvent;
import com.stockpulse.stockpulseAPI.domain.notification.service.NotificationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class NotificationEventHandler {
    private final NotificationService notificationService;

    public NotificationEventHandler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleMemberSavedEvent(MemberCreatedEvent event) {
        notificationService.initNotification(event.getMemberId());
    }

}
