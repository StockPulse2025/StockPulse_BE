package com.stockpulse.stockpulseAPI.domain.notification;

import com.stockpulse.stockpulseAPI.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
